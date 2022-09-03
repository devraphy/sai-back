package projectsai.saibackend.security.config;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;



@Configuration
@EnableWebMvc
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI(@Value("${springdoc.version}") String springDocVersion) {
        Info info = new Info()
                .title("Project SAI API Documentation")
                .version(springDocVersion)
                .description("Open API for project SAI")
                .termsOfService("http://swagger.io/terms/")
                .contact(new Contact()
                        .name("devRaphy")
                        .url("https://github.com/devraphy")
                        .email("devraphy@gmail.com"));

        return new OpenAPI()
                .components(new Components())
                .info(info);
    }
}
