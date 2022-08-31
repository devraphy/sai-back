package projectsai.saibackend.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import projectsai.saibackend.service.JwtCookieService;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j @RequiredArgsConstructor
@WebFilter(urlPatterns = "/api/*")
public class JwtFilter implements Filter {

    private final JwtCookieService jwtCookieService;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("JwtFilter init()");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest servletRequest = (HttpServletRequest) request;
        HttpServletResponse servletResponse = (HttpServletResponse) response;

        if(servletRequest.getRequestURI().equals("/api/join") ||
                servletRequest.getRequestURI().equals("/api/email/validation") ||
                servletRequest.getRequestURI().equals("/api/login")) {
            chain.doFilter(request, response);
        }

        else if(jwtCookieService.validateAccessToken(servletRequest, servletResponse)) {
            chain.doFilter(request, response);
        }
        else {
            response.setContentType(APPLICATION_JSON_VALUE);
            objectMapper.writeValue(servletResponse.getOutputStream(), new FilterResponse(Boolean.FALSE));
        }

    }

    @Override
    public void destroy() {
        log.info("JwtFilter destroy()");
    }
}