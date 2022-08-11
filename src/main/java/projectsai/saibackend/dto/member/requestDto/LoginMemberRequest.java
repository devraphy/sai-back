package projectsai.saibackend.dto.member.requestDto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data @NotNull
public class LoginMemberRequest {
    private String email;
    private String password;
}
