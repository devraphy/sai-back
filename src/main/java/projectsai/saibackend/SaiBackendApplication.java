package projectsai.saibackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan
public class SaiBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(SaiBackendApplication.class, args);
	}
}
