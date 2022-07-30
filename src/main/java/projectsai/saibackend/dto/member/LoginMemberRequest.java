package projectsai.saibackend.dto.member;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class LoginMemberRequest {
    @NotNull
    String email;
    @NotNull
    String password;
}
