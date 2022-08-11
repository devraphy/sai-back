package projectsai.saibackend.dto.member.requestDto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data @NotNull
public class JoinMemberRequest {
    private String email;
    private String name;
    private String password;
}
