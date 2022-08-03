package projectsai.saibackend.dto.member.responseDto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data @AllArgsConstructor
public class LoginMemberResponse {
    private Long id;
    private String email;
    private String name;
    private String password;
    private LocalDate signUpDate;
    private Boolean result;
}
