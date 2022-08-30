package projectsai.saibackend.api;

import com.fasterxml.jackson.databind.ObjectMapper;
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

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController @Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberApiController {

    private final MemberService memberService;
    private final JwtCookieService jwtCookieService;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/email/validation") // 이메일 중복 검증
    public void emailValidation(@RequestBody @Valid EmailValidationRequest request,
                                                   HttpServletResponse servletResp) throws IOException {
        String email = request.getEmail().toLowerCase();

        if(memberService.emailValidation(email)) {
            log.info("Member API | emailValidation() Success: 신규 이메일 확인");
            objectMapper.writeValue(servletResp.getOutputStream(), new MemberResultResponse(Boolean.TRUE));
        }
        else {
            log.warn("Member API | emailValidation() Fail: 중복된 이메일");
            servletResp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(servletResp.getOutputStream(), new MemberResultResponse(Boolean.FALSE));
        }
    }

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

    @GetMapping("/login") // refresh_token 검증을 이용한 로그인 검증
    public void tokenLogin(HttpServletRequest servletReq, HttpServletResponse servletResp) throws IOException {
        servletResp.setContentType(APPLICATION_JSON_VALUE);

        if(jwtCookieService.validateRefreshToken(servletReq)) {
            try {
                Cookie[] cookies = servletReq.getCookies();
                String refreshToken = cookies[1].getValue();

                String email = jwtProvider.getUserEmail(refreshToken);
                Member member = memberService.findByEmail(email);
                String role = member.getRole();

                jwtCookieService.setTokenInCookie(email, role, servletReq, servletResp);

                log.info("Member API | tokenLogin() Success: refresh 토큰 로그인 성공 및 토큰 갱신");
                objectMapper.writeValue(servletResp.getOutputStream(), new MemberResultResponse(Boolean.TRUE));
                return;
            }
            catch (Exception e) {
                log.error("Member API | tokenLogin() Fail: 에러 발생 => {}", e.getMessage());
            }
        }
        else {
            log.warn("Member API | tokenLogin() Fail: 토큰 로그인 실패");
            servletResp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(servletResp.getOutputStream(), new MemberResultResponse(Boolean.FALSE));
        }
    }

    @PostMapping("/login") // 로그인
    public void basicLogin(@RequestBody @Valid LoginMemberRequest requestDTO,
                           HttpServletRequest servletReq, HttpServletResponse servletResp) throws IOException {

        servletResp.setContentType(APPLICATION_JSON_VALUE);

        if(memberService.loginValidation(requestDTO.getEmail().toLowerCase(), requestDTO.getPassword())) {
            Member member = memberService.findByEmail(requestDTO.getEmail().toLowerCase());
            String email = member.getEmail();

            jwtCookieService.setTokenInCookie(email, member.getRole(), servletReq, servletResp);

            log.info("Member API | basicLogin() Success: 로그인 성공");
            objectMapper.writeValue(servletResp.getOutputStream(), new LoginMemberResponse(email, Boolean.TRUE));
        }
        else {
            log.warn("Member API | basicLogin() Fail: 로그인 실패");
        }
        servletResp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        objectMapper.writeValue(servletResp.getOutputStream(), new MemberResultResponse(Boolean.FALSE));
    }

    @GetMapping("/logout")
    public void logoutMember(HttpServletRequest servletReq, HttpServletResponse servletResp) throws IOException {
        servletResp.setContentType(APPLICATION_JSON_VALUE);

        if(jwtCookieService.validateAccessToken(servletReq, servletResp)) {
            try {
                log.info("Member API | memberLogout() Success: 로그아웃 성공");
                jwtCookieService.terminateCookie(servletResp);
                objectMapper.writeValue(servletResp.getOutputStream(), new MemberResultResponse(Boolean.TRUE));
                return;
            }
            catch (Exception e) {
                log.error("Member API | memberLogout() Fail: 에러 발생 => {}", e.getMessage());
            }
        }
        else {
            log.warn("Member API | memberLogout() Fail: 로그아웃 대상이 아니거나 모든 토큰이 만료");
        }
        servletResp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        objectMapper.writeValue(servletResp.getOutputStream(), new MemberResultResponse(Boolean.FALSE));
    }

    @GetMapping("/profile") // 회원 - 정보 조회
    public void showMember(HttpServletRequest servletReq, HttpServletResponse servletResp) throws IOException {

        servletResp.setContentType(APPLICATION_JSON_VALUE);

        if(jwtCookieService.validateAccessToken(servletReq, servletResp)) {
            try {
                Cookie[] cookies = servletReq.getCookies();
                String accessToken = cookies[0].getValue();

                String email = jwtProvider.getUserEmail(accessToken);
                Member member = memberService.findByEmail(email);

                log.info("Member API | showMember() Success: 프로필 요청 성공");
                objectMapper.writeValue(servletResp.getOutputStream(), SearchMemberResponse.buildResponse(member));
                return;
            }
            catch (Exception e) {
                log.error("Member API | showMember() Fail: 에러 발생 => {}", e.getMessage());
            }
        }
        else {
            log.warn("Member API | showMember() Fail: 프로필 요청 실패(모든 토큰 만료)");
        }
        servletResp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        objectMapper.writeValue(servletResp.getOutputStream(), new MemberResultResponse(Boolean.FALSE));
    }

    @PutMapping("/profile") // 회원 - 정보 수정
    public void updateMember(@RequestBody @Valid UpdateMemberRequest requestDTO,
                             HttpServletRequest servletReq, HttpServletResponse servletResp) throws IOException {

        servletResp.setContentType(APPLICATION_JSON_VALUE);

        if(jwtCookieService.validateAccessToken(servletReq, servletResp)) {
            boolean result = memberService.updateMember(requestDTO.getId(), requestDTO.getEmail().toLowerCase(),
                    requestDTO.getName(), passwordEncoder.encode(requestDTO.getPassword()));

            if(result) {
                log.info("Member API | updateMember() Success: 프로필 업데이트 성공");
                objectMapper.writeValue(servletResp.getOutputStream(), new MemberResultResponse(Boolean.TRUE));
                return;
            }
            log.warn("Member API | updateMember() Fail: 프로필 업데이트 실패");
        }
        else {
            log.warn("Member API | updateMember() Fail: 프로필 업데이트 실패(모든 토큰 만료)");
            servletResp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(servletResp.getOutputStream(), new MemberResultResponse(Boolean.FALSE));
        }
    }

    @DeleteMapping("/profile") // 회원 - 탈퇴
    public void deleteMember(@RequestBody @Valid DeleteMemberRequest requestDTO,
                             HttpServletRequest servletReq, HttpServletResponse servletResp) throws IOException {

        servletResp.setContentType(APPLICATION_JSON_VALUE);

        if(jwtCookieService.validateAccessToken(servletReq, servletResp)) {
            if(memberService.deleteMember(requestDTO.getEmail())) {
                log.info("Member API | deleteMember() Success: 탈퇴 성공");
                jwtCookieService.terminateCookie(servletResp);
                objectMapper.writeValue(servletResp.getOutputStream(), new MemberResultResponse(Boolean.TRUE));
                return;
            }
            log.warn("Member API | deleteMember() Fail: 탈퇴 실패");
        }
        else {
            log.warn("Member API | deleteMember() Fail: 탈퇴 실패(모든 토큰 만료)");
            servletResp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(servletResp.getOutputStream(), new MemberResultResponse(Boolean.FALSE));
        }
    }
}
