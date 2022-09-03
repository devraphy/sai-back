package projectsai.saibackend.dto.member.requestDto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@NotNull
public class LoginMemberRequest {
    @Schema(description = "이메일", example = "abc@gmail.com")
    private String email;

    @Schema(description = "비밀번호", example = "abcdef123")
    private String password;
}
