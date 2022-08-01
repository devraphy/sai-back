package projectsai.saibackend.dto.member;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class DeleteMemberRequest {
    @NotNull
    String email;
}
