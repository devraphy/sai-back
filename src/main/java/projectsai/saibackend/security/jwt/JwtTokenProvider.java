package projectsai.saibackend.security.jwt;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import projectsai.saibackend.domain.Role;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component @Slf4j
@RequiredArgsConstructor
public class JwtTokenProvider {

    private static final String SECRET_KEY = "asefidlowoijeiowgalseoifjdjkllksiefjdlksjfdlisefdjkldslifjekld";
    private long tokenValidTime = 30 * 1000 * 60;
    private String headerName = "authorization";

    private final UserDetailsService userDetailsService;

    public String createToken(String userEmail, List<Role> roles) {
        Map<String, Object> headers = new HashMap<>();
        headers.put("type", "JWT");
        headers.put("algorithm", "HS256");

        Date now = new Date();
        Claims claims = Jwts.claims().setSubject(userEmail);
        List<String> roleList = roles.stream().map(o -> o.getPosition()).collect(Collectors.toList());
        claims.put("roles", roleList);

        return Jwts.builder()
                .setHeader(headers)
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + tokenValidTime))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    // 토큰에서 회원 정보 추출
    public String getUserInfo(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // request header 에서 token 가져오기
    public String resolveToken(HttpServletRequest request) {
        String token = request.getHeader(headerName);

        if (!StringUtils.hasText(token)) return "";

        if (!Pattern.matches("^(?i)Bearer .*", token))
            throw new IllegalArgumentException("토큰값이 잘못되었습니다");

        return token.replaceAll("^(?i)Bearer( )*", "");
    }

    // 토큰의 유효성과 만료일자 확인
    public boolean validToken(String jwtToken) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(jwtToken)
                    .getBody();

            if (claims.getExpiration().before(new Date()))
                throw new IllegalStateException("만료된 토큰입니다");

            return true;
        } catch (Exception e) {
            log.error("토큰 유효성 확인에서 에러 발생 => {}", e.getMessage());
            return false;
        }
    }

    // JWT 토큰에서 인증 정보 조회
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getUserInfo(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }
}


