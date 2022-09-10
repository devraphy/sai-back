package projectsai.saibackend.service;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import projectsai.saibackend.security.jwt.JwtProvider;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
@Slf4j
@RequiredArgsConstructor
public class JwtCookieService {

    private final JwtProvider jwtProvider;

    // Token 담은 Cookie 객체 생성
    public Cookie createCookie(String name, String token, HttpServletRequest request) {
        Cookie cookie = new Cookie(name, token);
        cookie.setMaxAge(7 * 86400);
        cookie.setComment("JWT");
        cookie.setHttpOnly(true);
        cookie.setPath(request.getContextPath());
        return cookie;
    }

    // Access & Refresh Token을 생성하고 Cookie에 저장
    // Header에 Role을 저장
    public void setTokenInCookie(String email, String role, HttpServletRequest servletRequest,
                                 HttpServletResponse servletResponse) {

        String accessToken = jwtProvider.createAccessToken(email);
        String refreshToken = jwtProvider.createRefreshToken(email);

        Cookie access_cookie = createCookie("access_token", accessToken, servletRequest);
        Cookie refresh_cookie = createCookie("refresh_token", refreshToken, servletRequest);

        servletResponse.addCookie(access_cookie);
        servletResponse.addCookie(refresh_cookie);
        servletResponse.setHeader("role", role);
    }

    // Request의 Cookie(= JWT) 검증
    public boolean validateAccessToken(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        try {
            String accessToken = this.getAccessToken(servletRequest);
            String refreshToken = this.getRefreshToken(servletRequest);

            try {
                if (jwtProvider.validateToken(accessToken)) {
                    log.info("JwtCookieService | validateAccessToken() Success: Access 토큰 유효함");
                    return true;
                }
            } catch (ExpiredJwtException e) {
                log.warn("JwtCookieService | validateAccessToken() Fail: Access 토큰 만료됨 => {}", e.getMessage());
                if (jwtProvider.validateToken(refreshToken)) {
                    String userEmail = jwtProvider.getUserEmail(refreshToken);
                    String newAccessToken = jwtProvider.createAccessToken(userEmail);
                    Cookie access_cookie = createCookie("access_token", newAccessToken, servletRequest);
                    servletResponse.addCookie(access_cookie);
                    log.info("JwtCookieService | validateAccessToken(): Access 토큰 갱신");
                    return true;
                }
                log.warn("JwtCookieService | validateAccessToken() Fail: 모든 토큰 만료됨");
            }
        } catch (Exception e) {
            log.error("JwtCookieService | validateAccessToken() Fail: 에러 발생 => {}", e.getMessage());
        }
        return false;
    }

    // 자동 로그인 시 Request의 Cookie에서 Refresh Token 검증
    public boolean validateRefreshToken(HttpServletRequest servletRequest) {
        try {
            String refreshToken = this.getRefreshToken(servletRequest);

            if (jwtProvider.validateToken(refreshToken)) {
                log.info("JwtCookieService | validateRefreshToken() Success: Refresh 토큰 유효함");
                return true;
            } else {
                log.warn("JwtCookieService | validateRefreshToken() Fail: Refresh 토큰 검증 실패");
                return false;
            }
        } catch (ExpiredJwtException e) {
            log.error("JwtCookieService | validateRefreshToken() Fail: Refresh 토큰 만료됨 => {}", e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("JwtCookieService | validateRefreshToken() Fail: 에러 발생 => {}", e.getMessage());
            return false;
        }
    }

    // 로그아웃 또는 회원 탈퇴 시 Cookie와 Role을 말소
    public void terminateCookieAndRole(HttpServletResponse servletResponse) {
        Cookie accessCookie = new Cookie("access_token", null);
        Cookie refreshCookie = new Cookie("refresh_token", null);

        accessCookie.setMaxAge(0);
        refreshCookie.setMaxAge(0);

        servletResponse.addCookie(accessCookie);
        servletResponse.addCookie(refreshCookie);
        servletResponse.setHeader("role", null);
    }

    // Request의 Cookie로부터 AccessToken 가져오기
    public String getAccessToken(HttpServletRequest servletRequest) {
        return servletRequest.getCookies()[0].getValue();
    }

    // Request의 Cookie로부터 RefreshToken 가져오기
    public String getRefreshToken(HttpServletRequest servletRequest) {
        return servletRequest.getCookies()[1].getValue();
    }
}
