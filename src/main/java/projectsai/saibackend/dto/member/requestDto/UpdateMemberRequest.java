package projectsai.saibackend.dto.member.requestDto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data @NotNull
public class UpdateMemberRequest {
    @Schema(description = "변경할 이메일", example = "abc@gmail.com")
    private String email;

    @Schema(description = "변경할 이름", example = "곽두팔")
    private String name;

    @Schema(description = "변경할 비밀번호", example = "abcdef123")
    private String password;
}
