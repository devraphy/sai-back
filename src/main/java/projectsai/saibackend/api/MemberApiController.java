package projectsai.saibackend.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import projectsai.saibackend.domain.Member;
import projectsai.saibackend.dto.member.requestDto.*;
import projectsai.saibackend.dto.member.responseDto.*;
import projectsai.saibackend.security.jwt.JwtProvider;
import projectsai.saibackend.service.JwtCookieService;
import projectsai.saibackend.service.MemberService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController @Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
@ApiResponses({@ApiResponse(code = 500, message = "에러 발생"), @ApiResponse(code = 401, message = "토큰 검증 실패")})
public class MemberApiController {

    private final MemberService memberService;
    private final JwtCookieService jwtCookieService;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @ApiOperation(value = "이메일 중복 검증")
    @ApiResponse(code = 200, message = "사용 가능한 이메일")
    @PostMapping("/email/validation")
    public void emailValidation(@RequestBody @Valid EmailValidationRequest request,
                                HttpServletResponse servletResp) throws IOException {

        servletResp.setContentType(APPLICATION_JSON_VALUE);

        String email = request.getEmail().toLowerCase();

        if(memberService.emailValidation(email)) {
            log.info("Member API | emailValidation() Success: 신규 이메일 확인");
            objectMapper.writeValue(servletResp.getOutputStream(), new MemberResultResponse(Boolean.TRUE));
        }
        else {
            log.warn("Member API | emailValidation() Fail: 사용할 수 없는 이메일");
            servletResp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(servletResp.getOutputStream(), new MemberResultResponse(Boolean.FALSE));
        }
    }

    @ApiOperation(value = "회원 가입")
    @ApiResponses({@ApiResponse(code = 200, message = "회원 가입 성공"), @ApiResponse(code = 400, message = "회원 가입 실패")})
    @PostMapping("/join") // 회원 가입
    public void joinMember(@RequestBody @Valid JoinMemberRequest requestDTO,
                           HttpServletRequest servletReq, HttpServletResponse servletResp) throws IOException {

        Member member = new Member(requestDTO.getName(), requestDTO.getEmail().toLowerCase(),
                passwordEncoder.encode(requestDTO.getPassword()), Boolean.TRUE, "ROLE_USER");

        servletResp.setContentType(APPLICATION_JSON_VALUE);

        if(memberService.signUp(member)) {
            String email = member.getEmail();
            String role = member.getRole();

            jwtCookieService.setTokenInCookie(email, role, servletReq, servletResp);

            log.info("Member API | joinMember() Success: 회원 가입 성공");
            objectMapper.writeValue(servletResp.getOutputStream(),
                    new JoinMemberResponse(member.getMemberId(), Boolean.TRUE));
        }
        else {
            log.warn("Member API | joinMember() Fail: 회원 가입 실패");
            servletResp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(servletResp.getOutputStream(),
                    new MemberResultResponse(Boolean.FALSE));
        }
    }


    @ApiOperation(value = "자동 로그인", notes = "refresh_token 검증을 이용한 로그인")
    @ApiResponse(code = 200, message = "로그인 성공(토큰 갱신 및 Role 발행)")
    @GetMapping("/login")
    public void tokenLogin(HttpServletRequest servletReq, HttpServletResponse servletResp) throws IOException {

        servletResp.setContentType(APPLICATION_JSON_VALUE);

        try {
            String refreshToken = jwtCookieService.getRefreshToken(servletReq);
            String email = jwtProvider.getUserEmail(refreshToken);
            Member member = memberService.findByEmail(email);
            String role = member.getRole();

            jwtCookieService.setTokenInCookie(email, role, servletReq, servletResp);

            log.info("Member API | tokenLogin() Success: refresh 토큰 로그인 성공 및 토큰 갱신");
            objectMapper.writeValue(servletResp.getOutputStream(), new MemberResultResponse(Boolean.TRUE));
        }
        catch (Exception e) {
            log.error("Member API | tokenLogin() Fail: 에러 발생 => {}", e.getMessage());
            servletResp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(servletResp.getOutputStream(), new MemberResultResponse(Boolean.FALSE));
        }
    }

    @ApiOperation(value = "일반 로그인", notes = "ID와 PW 입력을 이용한 로그인")
    @ApiResponses({@ApiResponse(code = 200, message = "로그인 성공(토큰 및 Role 발행)"), @ApiResponse(code = 400, message = "로그인 실패")})
    @PostMapping("/login")
    public void basicLogin(@RequestBody @Valid LoginMemberRequest requestDTO,
                           HttpServletRequest servletReq, HttpServletResponse servletResp) throws IOException {

        servletResp.setContentType(APPLICATION_JSON_VALUE);

        if(memberService.loginValidation(requestDTO.getEmail().toLowerCase(), requestDTO.getPassword())) {
            Member member = memberService.findByEmail(requestDTO.getEmail().toLowerCase());
            String email = member.getEmail();

            jwtCookieService.setTokenInCookie(email, member.getRole(), servletReq, servletResp);

            log.info("Member API | basicLogin() Success: 로그인 성공");
            objectMapper.writeValue(servletResp.getOutputStream(), new LoginMemberResponse(email, Boolean.TRUE));
            return;
        }
        log.warn("Member API | basicLogin() Fail: 로그인 실패");
        servletResp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        objectMapper.writeValue(servletResp.getOutputStream(), new MemberResultResponse(Boolean.FALSE));
    }

    @ApiOperation(value = "로그아웃", notes = "모든 토큰 및 Role 말소")
    @ApiResponse(code = 200, message = "로그아웃 성공")
    @GetMapping("/logout")
    public void logoutMember(HttpServletResponse servletResp) throws IOException {

        servletResp.setContentType(APPLICATION_JSON_VALUE);

        try {
            log.info("Member API | logoutMember() Success: 로그아웃 성공");
            jwtCookieService.terminateCookie(servletResp);
            objectMapper.writeValue(servletResp.getOutputStream(), new MemberResultResponse(Boolean.TRUE));
            return;
        }
        catch (Exception e) {
            log.error("Member API | logoutMember() Fail: 에러 발생 => {}", e.getMessage());
            servletResp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(servletResp.getOutputStream(), new MemberResultResponse(Boolean.FALSE));
        }
    }

    @ApiOperation(value = "개인 정보 조회", notes = "access_token의 payload(email)를 이용한 회원 정보 검색")
    @ApiResponse(code = 200, message = "회원 정보 출력")
    @GetMapping("/profile") // 회원 - 정보 조회
    public void showMember(HttpServletRequest servletReq, HttpServletResponse servletResp) throws IOException {

        servletResp.setContentType(APPLICATION_JSON_VALUE);

        try {
            String accessToken = jwtCookieService.getAccessToken(servletReq);
            String email = jwtProvider.getUserEmail(accessToken);
            Member member = memberService.findByEmail(email);

            log.info("Member API | showMember() Success: 프로필 요청 성공");
            objectMapper.writeValue(servletResp.getOutputStream(), SearchMemberResponse.buildResponse(member));
        }
        catch (Exception e) {
            log.error("Member API | showMember() Fail: 에러 발생 => {}", e.getMessage());
            servletResp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(servletResp.getOutputStream(), new MemberResultResponse(Boolean.FALSE));
        }

    }

    @ApiOperation(value = "개인 정보 변경", notes = "이메일, 비밀번호 변경 가능")
    @ApiResponses({@ApiResponse(code = 200, message = "변경 완료"), @ApiResponse(code = 400, message = "변경 실패")})
    @PutMapping("/profile")
    public void updateMember(@RequestBody @Valid UpdateMemberRequest requestDTO,
                             HttpServletRequest servletReq, HttpServletResponse servletResp) throws IOException {

        servletResp.setContentType(APPLICATION_JSON_VALUE);

        String accessToken = jwtCookieService.getAccessToken(servletReq);
        String userEmail = jwtProvider.getUserEmail(accessToken);
        Member member = memberService.findByEmail(userEmail);

        boolean result = memberService.updateMember(member.getMemberId(), requestDTO.getEmail().toLowerCase(),
                requestDTO.getName(), passwordEncoder.encode(requestDTO.getPassword()));

        if(result) {
            log.info("Member API | updateMember() Success: 프로필 업데이트 성공");
            jwtCookieService.setTokenInCookie(requestDTO.getEmail(), "ROLE_USER", servletReq, servletResp);
            objectMapper.writeValue(servletResp.getOutputStream(), new MemberResultResponse(Boolean.TRUE));
            return;
        }

        log.warn("Member API | updateMember() Fail: 프로필 업데이트 실패");
        servletResp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        objectMapper.writeValue(servletResp.getOutputStream(), new MemberResultResponse(Boolean.FALSE));
    }

    @ApiOperation(value = "회원 탈퇴", notes = "회원 탈퇴 시 모든 토큰 및 Role 말소")
    @ApiResponses({@ApiResponse(code = 200, message = "탈퇴 완료"), @ApiResponse(code = 400, message = "탈퇴 실패")})
    @DeleteMapping("/profile") // 회원 - 탈퇴
    public void deleteMember(@RequestBody @Valid DeleteMemberRequest requestDTO,
                             HttpServletResponse servletResp) throws IOException {

        servletResp.setContentType(APPLICATION_JSON_VALUE);

        if(memberService.deleteMember(requestDTO.getEmail())) {
            log.info("Member API | deleteMember() Success: 탈퇴 성공");
            jwtCookieService.terminateCookie(servletResp);
            objectMapper.writeValue(servletResp.getOutputStream(), new MemberResultResponse(Boolean.TRUE));
            return;
        }

        log.warn("Member API | deleteMember() Fail: 탈퇴 실패");
        servletResp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        objectMapper.writeValue(servletResp.getOutputStream(), new MemberResultResponse(Boolean.FALSE));
    }
}
