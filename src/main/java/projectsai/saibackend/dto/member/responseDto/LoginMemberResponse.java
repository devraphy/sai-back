package projectsai.saibackend.dto.member.responseDto;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data @AllArgsConstructor
public class LoginMemberResponse {
    private String email;
    private Boolean result;
}
