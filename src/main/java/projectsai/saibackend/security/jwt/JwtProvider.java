package projectsai.saibackend.security.jwt;


import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Component @Slf4j
@RequiredArgsConstructor
public class JwtProvider {
    @Value("${JWT_SECRET_KEY}")
    private String jwt_secret_key;

    @PostConstruct
    protected void init() {
        jwt_secret_key = Base64.getEncoder().encodeToString(jwt_secret_key.getBytes());
    }

    // Access 토큰 생성
    public String createAccessToken(String email) {
        Date now = new Date(System.currentTimeMillis());
        return Jwts.builder()
                .setHeaderParam("typ","access")
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + 1 * 86400 * 1000))
                .signWith(SignatureAlgorithm.HS256, jwt_secret_key)
                .compact();
    }

    // Refresh 토큰 생성
    public String createRefreshToken(String email) {
        Date now = new Date(System.currentTimeMillis());
        return Jwts.builder()
                .setHeaderParam("typ","refresh")
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime()+ 7 * 86400 * 1000))
                .signWith(SignatureAlgorithm.HS256, jwt_secret_key)
                .compact();
    }

    // JWT 토큰에서 인증 정보 조회
    public boolean getAuthentication(String token, String email) {
        String subject = Jwts.parser()
                .setSigningKey(jwt_secret_key)
                .parseClaimsJws(token)
                .getBody().getSubject();

        if(subject.equals(email)) {
            return true;
        }
        return false;
    }

    // 토큰에서 회원 email 추출
    public String getUserEmail(String token) {
        return Jwts.parser()
                .setSigningKey(jwt_secret_key)
                .parseClaimsJws(token)
                .getBody().getSubject();
    }

    // Jwt 토큰 유효성 검사
    public boolean validateToken(String token) throws ExpiredJwtException {
        try {
            Jwts.parser().setSigningKey(jwt_secret_key).parseClaimsJws(token);
            return true;
        }
        catch (SignatureException e) {
            log.error("Invalid JWT signature => {}", e.getMessage());
        }
        catch (MalformedJwtException e) {
            log.error("Invalid JWT token => {}", e.getMessage());
        }
        catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token => {}", e.getMessage());
        }
        catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty. => {}", e.getMessage());
        }
        return false;
    }
}


