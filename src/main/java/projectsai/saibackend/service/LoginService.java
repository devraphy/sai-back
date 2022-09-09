package projectsai.saibackend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import projectsai.saibackend.domain.Member;
import projectsai.saibackend.dto.member.requestDto.LoginMemberRequest;
import projectsai.saibackend.dto.member.responseDto.LoginMemberResponse;
import projectsai.saibackend.dto.member.responseDto.MemberResultResponse;
import projectsai.saibackend.exception.ErrorCode;
import projectsai.saibackend.exception.ErrorResponse;
import projectsai.saibackend.security.jwt.JwtProvider;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LoginService {

    private final JwtCookieService jwtCookieService;
    private final JwtProvider jwtProvider;
    private final MemberService memberService;
    private final ObjectMapper objectMapper;

    // MemberApi - 자동 로그인
    public void autoLoginApi(HttpServletRequest servletReq, HttpServletResponse servletResp) throws IOException {

        servletResp.setContentType(APPLICATION_JSON_VALUE);

        if (jwtCookieService.validateRefreshToken(servletReq)) {
            try {
                String refreshToken = jwtCookieService.getRefreshToken(servletReq);
                String email = jwtProvider.getUserEmail(refreshToken);
                Member member = memberService.findByEmail(email);
                String role = member.getRole();

                jwtCookieService.setTokenInCookie(email, role, servletReq, servletResp);

                log.info("Login Service | autoLoginApi() Success: refresh 토큰 로그인 성공 및 토큰 갱신");
                objectMapper.writeValue(servletResp.getOutputStream(), new MemberResultResponse(Boolean.TRUE));

            } catch (Exception e) {
                log.error("Login Service | autoLoginApi() Fail: 에러 발생 => {}", e.getMessage());
                servletResp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                objectMapper.writeValue(servletResp.getOutputStream(),
                        new ErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR));
            }
        } else {
            log.warn("Login Service | autoLoginApi() Fail: 토큰 검증 실패");
            servletResp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            objectMapper.writeValue(servletResp.getOutputStream(), new ErrorResponse(ErrorCode.UNAUTHORIZED));
        }
    }

    // MemberApi - 로그아웃
    public void logoutApi(HttpServletResponse servletResp) throws IOException {

        servletResp.setContentType(APPLICATION_JSON_VALUE);

        try {
            log.info("Login Service | logoutApi() Success: 로그아웃 성공");
            jwtCookieService.terminateCookieAndRole(servletResp);
            objectMapper.writeValue(servletResp.getOutputStream(), new MemberResultResponse(Boolean.TRUE));
        } catch (Exception e) {
            log.error("Login Service | logoutApi() Fail: 에러 발생 => {}", e.getMessage());
            servletResp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(servletResp.getOutputStream(),
                    new ErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR));
        }
    }

    // MemberApi - 일반 로그인
    public void basicLoginApi(LoginMemberRequest requestDTO, HttpServletRequest servletReq, HttpServletResponse servletResp) throws IOException {

        servletResp.setContentType(APPLICATION_JSON_VALUE);

        try {
            if (memberService.loginValidation(requestDTO.getEmail().toLowerCase(), requestDTO.getPassword())) {
                Member member = memberService.findByEmail(requestDTO.getEmail().toLowerCase());
                String email = member.getEmail();

                jwtCookieService.setTokenInCookie(email, member.getRole(), servletReq, servletResp);

                log.info("Login Service | basicLoginApi() Success: 로그인 성공");
                objectMapper.writeValue(servletResp.getOutputStream(), new LoginMemberResponse(email, Boolean.TRUE));
            } else {
                log.warn("Login Service | basicLoginApi() Fail: 로그인 실패");
                servletResp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                objectMapper.writeValue(servletResp.getOutputStream(), new ErrorResponse(ErrorCode.BAD_REQUEST));
            }
        }
        catch (Exception e) {
            log.warn("Login Service | basicLoginApi() Fail: 에러 발생 => {}", e.getMessage());
            servletResp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(servletResp.getOutputStream(),
                    new ErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR));
        }
    }
}

