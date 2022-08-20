package projectsai.saibackend;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import projectsai.saibackend.domain.Member;
import projectsai.saibackend.domain.Role;
import projectsai.saibackend.service.MemberService;

import java.util.ArrayList;

@SpringBootApplication
public class SaiBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(SaiBackendApplication.class, args);
	}
}
