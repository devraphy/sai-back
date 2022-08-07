package projectsai.saibackend.dto.member.requestDto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class JoinMemberRequest {
    @NotNull
    private String email;
    @NotNull
    private String name;
    @NotNull
    private String password;
}
