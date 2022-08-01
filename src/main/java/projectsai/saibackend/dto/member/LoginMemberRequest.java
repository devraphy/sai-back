package projectsai.saibackend.dto.member;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class LoginMemberRequest {
    @NotNull
    private String email;
    @NotNull
    private String password;
}
