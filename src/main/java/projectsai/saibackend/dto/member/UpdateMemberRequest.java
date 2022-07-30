package projectsai.saibackend.dto.member;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UpdateMemberRequest {
    @NotNull
    private Long id;
    @NotNull
    private String email;
    @NotNull
    private String name;
    @NotNull
    private String password;
}
