package projectsai.saibackend.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Member API", description = "회원 관련 CRUD 기능을 제공합니다.")
@RestController @Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
@ApiResponses({@ApiResponse(responseCode = "500", description = "에러 발생"), @ApiResponse(responseCode = "401", description = "토큰 검증 실패")})
public class MemberApiController {

    private final MemberService memberService;
    private final JwtCookieService jwtCookieService;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Operation(summary = "이메일 중복 검증", description = "회원가입 페이지에서 이메일 중복 검사에서 사용됩니다.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "이메일 사용 가능"), @ApiResponse(responseCode = "400", description = "이메일 사용 불가")})
    @PostMapping("/email/validation")
    public void emailValidation(@RequestBody @Valid EmailValidationRequest request,
                                HttpServletResponse servletResp) throws IOException {

        servletResp.setContentType(APPLICATION_JSON_VALUE);

        String email = request.getEmail().toLowerCase();

        if(memberService.emailValidation(email)) {
            log.info("Member API | emailValidation() Success: 중복 검증 통과");
            objectMapper.writeValue(servletResp.getOutputStream(), new MemberResultResponse(Boolean.TRUE));
        }
        else {
            log.warn("Member API | emailValidation() Fail: 중복 검증 실패");
            servletResp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(servletResp.getOutputStream(), new MemberResultResponse(Boolean.FALSE));
        }
    }

    @Operation(summary = "회원 가입")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "회원 가입 완료"), @ApiResponse(responseCode = "400", description = "회원 가입 실패")})
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

            log.info("Member API | joinMember() Success: 회원 가입 완료");
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


    @Operation(summary = "자동 로그인", description = "refresh_token 검증을 이용한 로그인")
    @ApiResponse(responseCode = "200", description = "로그인 성공(토큰 갱신 및 Role 발행)")
    @Parameters({@Parameter(name = "access_token", description = "회원가입 시 발행되는 쿠키를 사용합니다.", in = ParameterIn.COOKIE),
            @Parameter(name = "refresh_token", description = "회원가입 시 발행되는 쿠키를 사용합니다.", in = ParameterIn.COOKIE)})
    @GetMapping("/login")
    public void tokenLogin(HttpServletRequest servletReq, HttpServletResponse servletResp) throws IOException {

        servletResp.setContentType(APPLICATION_JSON_VALUE);

        if(jwtCookieService.validateRefreshToken(servletReq)) {
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
        else {
            log.warn("Member API | tokenLogin() Fail: refresh 토큰 만료");
            servletResp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(servletResp.getOutputStream(), new MemberResultResponse(Boolean.FALSE));
        }
    }

    @Operation(summary = "일반 로그인", description = "ID와 PW 입력을 이용한 로그인")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "로그인 성공(토큰 및 Role 발행)"), @ApiResponse(responseCode = "400", description = "로그인 실패")})
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

    @Operation(summary = "로그아웃", description = "모든 토큰 및 Role 말소")
    @ApiResponse(responseCode = "200", description = "로그아웃 성공")
    @Parameters({@Parameter(name = "access_token", description = "로그인 시 발행되는 쿠키를 사용합니다.", in = ParameterIn.COOKIE),
            @Parameter(name = "refresh_token", description = "로그인 시 발행되는 쿠키를 사용합니다.", in = ParameterIn.COOKIE)})
    @GetMapping("/logout")
    public void logoutMember(HttpServletResponse servletResp) throws IOException {
        servletResp.setContentType(APPLICATION_JSON_VALUE);

        try {
            log.info("Member API | logoutMember() Success: 로그아웃 성공");
            jwtCookieService.terminateCookie(servletResp);
            objectMapper.writeValue(servletResp.getOutputStream(), new MemberResultResponse(Boolean.TRUE));
        }
        catch (Exception e) {
            log.error("Member API | logoutMember() Fail: 에러 발생 => {}", e.getMessage());
            servletResp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(servletResp.getOutputStream(), new MemberResultResponse(Boolean.FALSE));
        }
    }

    @Operation(summary = "개인 정보 조회", description = "access_token의 payload(email)를 이용한 회원 정보 검색")
    @ApiResponse(responseCode = "200", description = "회원 정보 출력")
    @Parameters({@Parameter(name = "access_token", description = "로그인 시 발행되는 쿠키를 사용합니다.", in = ParameterIn.COOKIE),
            @Parameter(name = "refresh_token", description = "로그인 시 발행되는 쿠키를 사용합니다.", in = ParameterIn.COOKIE)})
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

    @Operation(summary = "개인 정보 변경", description = "이메일, 비밀번호 변경 가능")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "변경 완료"), @ApiResponse(responseCode = "400", description = "변경 실패")})
    @Parameters({@Parameter(name = "access_token", description = "로그인 시 발행되는 쿠키를 사용합니다.", in = ParameterIn.COOKIE),
            @Parameter(name = "refresh_token", description = "로그인 시 발행되는 쿠키를 사용합니다.", in = ParameterIn.COOKIE)})
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
            log.info("Member API | updateMember() Success: 프로필 업데이트 완료");
            jwtCookieService.setTokenInCookie(requestDTO.getEmail(), "ROLE_USER", servletReq, servletResp);
            objectMapper.writeValue(servletResp.getOutputStream(), new MemberResultResponse(Boolean.TRUE));
            return;
        }

        log.warn("Member API | updateMember() Fail: 프로필 업데이트 실패");
        servletResp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        objectMapper.writeValue(servletResp.getOutputStream(), new MemberResultResponse(Boolean.FALSE));
    }

    @Operation(summary = "회원 탈퇴", description = "회원 탈퇴 시 모든 토큰 및 Role 말소")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "탈퇴 완료"), @ApiResponse(responseCode = "400", description = "탈퇴 실패")})
    @Parameters({@Parameter(name = "access_token", description = "로그인 시 발행되는 쿠키를 사용합니다.", in = ParameterIn.COOKIE),
            @Parameter(name = "refresh_token", description = "로그인 시 발행되는 쿠키를 사용합니다.", in = ParameterIn.COOKIE)})
    @DeleteMapping("/profile") // 회원 - 탈퇴
    public void deleteMember(@RequestBody @Valid DeleteMemberRequest requestDTO,
                             HttpServletResponse servletResp) throws IOException {

        servletResp.setContentType(APPLICATION_JSON_VALUE);

        if(memberService.deleteMember(requestDTO.getEmail())) {
            log.info("Member API | deleteMember() Success: 탈퇴 완료");
            jwtCookieService.terminateCookie(servletResp);
            objectMapper.writeValue(servletResp.getOutputStream(), new MemberResultResponse(Boolean.TRUE));
            return;
        }

        log.warn("Member API | deleteMember() Fail: 탈퇴 실패");
        servletResp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        objectMapper.writeValue(servletResp.getOutputStream(), new MemberResultResponse(Boolean.FALSE));
    }
}
