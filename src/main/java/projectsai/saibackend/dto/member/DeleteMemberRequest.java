package projectsai.saibackend.dto.member;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class DeleteMemberRequest {
    @NotNull
    private String email;
}
