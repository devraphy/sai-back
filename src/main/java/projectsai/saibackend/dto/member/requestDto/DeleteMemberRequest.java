package projectsai.saibackend.dto.member.requestDto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class DeleteMemberRequest {
    @NotNull
    private String email;
}
