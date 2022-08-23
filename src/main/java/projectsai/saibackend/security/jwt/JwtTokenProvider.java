package projectsai.saibackend.security.jwt;


import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Component @Slf4j
@RequiredArgsConstructor
public class JwtTokenProvider {
    @Value("${JWT_SECRET_KEY}")
    private String jwt_secret_key;

    // 토큰 유효시간 30분
    private final long tokenValidTime = 1 * 60 * 1000L;
    private final UserDetailsService userDetailsService;

    @PostConstruct
    protected void init() {
        jwt_secret_key = Base64.getEncoder().encodeToString(jwt_secret_key.getBytes());
    }

    // JWT 토큰 생성
    public String createToken(String email, String role) {
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("role", role);
        Date now = new Date(System.currentTimeMillis());
        return Jwts.builder()
                .setHeaderParam("typ","jwt")
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + tokenValidTime))
                .signWith(SignatureAlgorithm.HS256, jwt_secret_key)
                .compact();
    }

    // JWT 토큰에서 인증 정보 조회 (이거 필요한가?)
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

    // 토큰에서 회원 정보 추출
    public String getUserPk(String token) {
        return Jwts.parser()
                .setSigningKey(jwt_secret_key)
                .parseClaimsJws(token)
                .getBody().getSubject();
    }

    // Request의 Header에서 token 값을 가져옴. "X-AUTH-TOKEN" : "TOKEN값'
    public String resolveToken(HttpServletRequest request) {
        return request.getHeader("X-AUTH-TOKEN");
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

    // 토큰 만료 검사
    public boolean validateExpiration(String token) {
        try {
            Jwts.parser().setSigningKey(jwt_secret_key).parseClaimsJws(token);
            return true;
        }
        catch(ExpiredJwtException e) {
            log.warn("validateExpiration() | 토큰이 만료되었습니다. => {}", e.getMessage());
            return false;
        }
    }
}


