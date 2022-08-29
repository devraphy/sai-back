package projectsai.saibackend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import projectsai.saibackend.security.jwt.JwtProvider;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service @Slf4j
@RequiredArgsConstructor
public class JwtCookieService {

    private final JwtProvider jwtProvider;

    // Token 담은 Cookie 객체 생성
    public Cookie createCookie(String name, String token, HttpServletRequest request) {
        Cookie cookie = new Cookie(name, token);
        cookie.setMaxAge(7 * 86400);
        cookie.setComment("JWT");
        return cookie;
    }

    // Token 발행 및 이를 담은 Cookie 객체를 생성 후 HttpServletResponse 안에 저장
    public void setTokenInCookie(String email, String role, HttpServletRequest servletRequest,
                                  HttpServletResponse servletResponse) {

        String accessToken = jwtProvider.createAccessToken(email);
        String refreshToken = jwtProvider.createRefreshToken(email);

        Cookie access_cookie = createCookie("access_token", accessToken, servletRequest);
        Cookie refresh_cookie = createCookie("refresh_token", refreshToken, servletRequest);

        servletResponse.addCookie(access_cookie);
        servletResponse.addCookie(refresh_cookie);
        servletResponse.setHeader("Role", role);
    }

    // HttpServletRequest 내부의 Cookie(= 토큰 값) 검증
    public boolean validateCookie(HttpServletRequest servletRequest) {
        Cookie[] cookies = servletRequest.getCookies();
        String accessToken = cookies[0].getValue();
        String refreshToken = cookies[1].getValue();

        if(jwtProvider.validateToken(accessToken) && jwtProvider.validateToken(refreshToken)) {
            return true;
        }
        return false;
    }

    // Client(= 브라우저) 쿠키 말소
    public void terminateCookie(HttpServletResponse servletResponse) {
        Cookie accessCookie = new Cookie("access_token", null);
        Cookie refreshCookie = new Cookie("refresh_token", null);

        accessCookie.setMaxAge(0);
        refreshCookie.setMaxAge(0);

        servletResponse.addCookie(accessCookie);
        servletResponse.addCookie(refreshCookie);
        servletResponse.setHeader("Role", null);
    }
}
