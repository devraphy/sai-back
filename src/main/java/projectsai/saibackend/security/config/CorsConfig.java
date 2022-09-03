package projectsai.saibackend.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true); // 서버의 response를 js로 요청 및 처리 가능하게 할지의 여부
        config.addAllowedOrigin("*"); // 모든 URL의 요청을 허용
        config.addAllowedHeader("*"); // 모든 Header에 응답을 허용
        config.addAllowedMethod("*"); // 모든 Http method를 허용
        source.registerCorsConfiguration("/api/**", config);
        return new CorsFilter(source);
    }
}
