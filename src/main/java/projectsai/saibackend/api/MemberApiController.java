package projectsai.saibackend.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import projectsai.saibackend.domain.Member;
import projectsai.saibackend.dto.member.requestDto.*;
import projectsai.saibackend.dto.member.responseDto.*;
import projectsai.saibackend.security.jwt.JwtProvider;
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
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/email/validation") // 이메일 중복 검증
    public EmailValidationResponse emailValidation(@RequestBody @Valid EmailValidationRequest request) {
        String email = request.getEmail().toLowerCase();

        if(memberService.emailValidation(email)) {
            log.info("Member API | emailValidation() Success: 신규 이메일 확인");
            return new EmailValidationResponse(email, Boolean.TRUE);
        }
        else {
            log.warn("Member API | emailValidation() Fail: 중복된 이메일");
            return new EmailValidationResponse(email, Boolean.FALSE);
        }
    }

    @PostMapping("/join") // 회원 가입
    public void joinMember(@RequestBody @Valid JoinMemberRequest requestDTO, HttpServletRequest servletReq,
                           HttpServletResponse servletResp) throws IOException {

        Member member = new Member(requestDTO.getName(), requestDTO.getEmail().toLowerCase(),
                passwordEncoder.encode(requestDTO.getPassword()), Boolean.TRUE, "ROLE_USER");

        servletResp.setContentType(APPLICATION_JSON_VALUE);

        if(memberService.signUp(member)) {
            String email = member.getEmail();
            String role = member.getRole();

            setTokenInCookie(email, role, servletReq, servletResp);

            log.info("Member API | joinMember() Success: 회원 가입 성공");
            objectMapper.writeValue(servletResp.getOutputStream(),
                    new JoinMemberResponse(member.getMemberId(), Boolean.TRUE));
        }
        else {
            log.warn("Member API | joinMember() Fail: 회원 가입 실패");
            servletResp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(servletResp.getOutputStream(),
                    new JoinMemberResponse(null, Boolean.FALSE));
        }
    }

    @GetMapping("/login") // refresh_token 검증을 이용한 로그인 검증
    public void tokenLogin(HttpServletRequest servletReq, HttpServletResponse servletResp) throws IOException {
        servletResp.setContentType(APPLICATION_JSON_VALUE);

        Cookie[] cookies = servletReq.getCookies();
        if(cookies != null) {
            String refreshToken = cookies[1].getValue();

            try {
                if(jwtProvider.validateToken(refreshToken)) {
                    String email = jwtProvider.getUserEmail(refreshToken);
                    Member member = memberService.findByEmail(email);
                    String role = member.getRole();

                    setTokenInCookie(email, role, servletReq, servletResp);
                    servletResp.setStatus(HttpServletResponse.SC_OK);

                    log.info("Member API | tokenLogin() Success: refresh 토큰으로 로그인 성공 및 모든 토큰 갱신");
                    objectMapper.writeValue(servletResp.getOutputStream(), new TokenLoginResponse(Boolean.TRUE));
                }
            }
            catch (ExpiredJwtException e) {
                log.warn("Member API | tokenLogin() Fail: 모든 토큰 만료됨 => {}", e.getMessage());
            }
        }
        else {
            log.warn("Member API | tokenLogin() Fail: 토큰으로 로그인 실패");
            servletResp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(servletResp.getOutputStream(), new TokenLoginResponse(Boolean.FALSE));
        }
    }

    @PostMapping("/login") // 로그인
    public void basicLogin(@RequestBody @Valid LoginMemberRequest requestDTO, HttpServletRequest servletReq,
                           HttpServletResponse servletResp) throws IOException {

        servletResp.setContentType(APPLICATION_JSON_VALUE);

        if(memberService.loginValidation(requestDTO.getEmail().toLowerCase(), requestDTO.getPassword())) {

            Member member = memberService.findByEmail(requestDTO.getEmail().toLowerCase());
            String email = member.getEmail();

            setTokenInCookie(email, member.getRole(), servletReq, servletResp);
            servletResp.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(servletResp.getOutputStream(), new LoginMemberResponse(email, Boolean.TRUE));
        }

        else {
            log.warn("Member API | loginMember() Fail: 로그인 실패");
            servletResp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(servletResp.getOutputStream(),
                    new LoginMemberResponse(null, Boolean.FALSE));
        }
    }

    // 이미 로그인을 한 상태이니까 토큰 값만 검증
    // 프론트에서 role 확인해서 못들어오게 막을 것 ==> 이렇게 하려면 Role을 쿠키에 저장해야 하지 않을까?
    @GetMapping("/profile") // 회원 - 정보 조회
    public void searchMember(HttpServletRequest servletReq, HttpServletResponse servletResp) throws IOException {

        servletResp.setContentType(APPLICATION_JSON_VALUE);

        if(validateTokenInCookie(servletReq)) {
            Cookie[] cookies = servletReq.getCookies();
            String accessToken = cookies[0].getValue();

            String email = jwtProvider.getUserEmail(accessToken);
            Member member = memberService.findByEmail(email);

            objectMapper.writeValue(servletResp.getOutputStream(), SearchMemberResponse.buildResponse(member));
        }

        else {
            // HTTP Status 의미에 대해서 더 알아보자. BAD REQUEST 말고 사용할 수 있는 상태코드가 있을거야
            servletResp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(servletResp.getOutputStream(),
                    new SearchMemberResponse(null, null, null, null, Boolean.FALSE));
        }
    }

    @PutMapping("/profile") // 회원 - 정보 수정
    public void updateMember(@RequestBody @Valid UpdateMemberRequest requestDTO, HttpServletRequest servletReq,
                             HttpServletResponse servletResp) throws IOException {

        servletResp.setContentType(APPLICATION_JSON_VALUE);

        if(validateTokenInCookie(servletReq)) {
            boolean result = memberService.updateMember(requestDTO.getId(), requestDTO.getEmail().toLowerCase(),
                    requestDTO.getName(), passwordEncoder.encode(requestDTO.getPassword()));

            if(result) {
                log.info("Member API | updateMember() Success: 프로필 수정 성공");
                objectMapper.writeValue(servletResp.getOutputStream(), new UpdateMemberResponse(Boolean.TRUE));
            }
        }

        else {
            log.warn("Member API | updateMember() Fail: 프로필 수정 실패");
            servletResp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(servletResp.getOutputStream(), new UpdateMemberResponse(Boolean.FALSE));
        }
    }

    @DeleteMapping("/profile") // 회원 - 탈퇴
    public void deleteMember(@RequestBody @Valid DeleteMemberRequest requestDTO, HttpServletRequest servletReq,
                                             HttpServletResponse servletResp) throws IOException {

        servletResp.setContentType(APPLICATION_JSON_VALUE);

        if(validateTokenInCookie(servletReq)) {
            if(memberService.deleteMember(requestDTO.getEmail())) {
                log.info("Member API | deleteMember() Success: 탈퇴 성공");
                Cookie accessCookie = new Cookie("access_token", null);
                Cookie refreshCookie = new Cookie("refresh_token", null);

                accessCookie.setMaxAge(0);
                refreshCookie.setMaxAge(0);

                servletResp.addCookie(accessCookie);
                servletResp.addCookie(refreshCookie);

                objectMapper.writeValue(servletResp.getOutputStream(), new DeleteMemberResponse(Boolean.TRUE));
            }
        }
        else {
            log.warn("Member API | deleteMember() Fail: 탈퇴 실패");
            servletResp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(servletResp.getOutputStream(), new DeleteMemberResponse(Boolean.FALSE));
        }
    }


    // ================================= 비지니스 메서드 =================================

    // Token을 담은 Cookie 객체를 생성하는 기능
    private Cookie tokenToCookie(String name, String token, HttpServletRequest request) {
        Cookie cookie = new Cookie(name, token);
        cookie.setPath(request.getContextPath());
        cookie.setMaxAge(7 * 86400);
        return cookie;
    }

    // Token을 발행하고 이를 담은 Cookie 객체를 생성한 다음 HttpSerletResponse에 저장하는 기능
    private void setTokenInCookie(String email, String role, HttpServletRequest servletReq,
                                  HttpServletResponse servletResp) {

        String accessToken = jwtProvider.createAccessToken(email);
        String refreshToken = jwtProvider.createRefreshToken(email);

        Cookie access_cookie = tokenToCookie("access_token", accessToken, servletReq);
        Cookie refresh_cookie = tokenToCookie("refresh_token", refreshToken, servletReq);

        servletResp.addCookie(access_cookie);
        servletResp.addCookie(refresh_cookie);
        servletResp.setHeader("Role", role);
    }

    // SHttpServletRequest로부터 전달된 Cookie 내부의 토큰 값을 검증하는 기능
    private boolean validateTokenInCookie(HttpServletRequest servletReq) {
        Cookie[] cookies = servletReq.getCookies();
        String accessToken = cookies[0].getValue();
        String refreshToken = cookies[1].getValue();

        if(jwtProvider.validateToken(accessToken) && jwtProvider.validateToken(refreshToken)) {
            return true;
        }
        return false;
    }

}
