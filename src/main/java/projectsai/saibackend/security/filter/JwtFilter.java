package projectsai.saibackend.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import projectsai.saibackend.exception.ErrorCode;
import projectsai.saibackend.exception.ErrorResponse;
import projectsai.saibackend.service.JwtCookieService;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RequiredArgsConstructor
@WebFilter(urlPatterns = "/api/*")
public class JwtFilter implements Filter {

    private final JwtCookieService jwtCookieService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("JwtFilter init()");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest servletRequest = (HttpServletRequest) request;
        HttpServletResponse servletResponse = (HttpServletResponse) response;

        /* 해당 URL로 요청할 시에는 Cookie의 JWT 검증 안함 */
        if (servletRequest.getRequestURI().equals("/api/join") ||
                servletRequest.getRequestURI().equals("/api/email/validation") ||
                servletRequest.getRequestURI().equals("/api/login")) {
            chain.doFilter(request, response);
        }

        /* 그 외 URL로 요청할 시에는 Cookie의 JWT 검증 */
        else if (jwtCookieService.validateAccessToken(servletRequest, servletResponse)) {
            log.info("JwtFilter | Success: 토큰 검증 완료");
            chain.doFilter(request, response);
        }

        /* Cookie의 JWT 검증 실패한 경우 */
        else {
            log.error("JwtFilter | Fail: 토큰 검증 실패");
            servletResponse.setContentType(APPLICATION_JSON_VALUE);
            servletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            objectMapper.writeValue(servletResponse.getOutputStream(), new ErrorResponse(ErrorCode.UNAUTHORIZED));
        }
    }

    @Override
    public void destroy() {
        log.info("JwtFilter destroy()");
    }
}
