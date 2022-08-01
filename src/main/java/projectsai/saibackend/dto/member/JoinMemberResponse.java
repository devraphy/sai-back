package projectsai.saibackend.dto.member;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JoinMemberResponse {
    private Long id;
    private Boolean result;
}