package projectsai.saibackend.dto.member;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class DeleteMemberResponse {
    private Boolean result;
}
