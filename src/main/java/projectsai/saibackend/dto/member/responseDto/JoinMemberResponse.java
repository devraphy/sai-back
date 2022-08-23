package projectsai.saibackend.dto.member.responseDto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JoinMemberResponse {
    private Long id;
    private String token;
    private Boolean result;
}