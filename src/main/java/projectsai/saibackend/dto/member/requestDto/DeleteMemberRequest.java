package projectsai.saibackend.dto.member.requestDto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data @NotNull
public class DeleteMemberRequest {
    @Schema(description = "삭제할 사용자 이메일", example = "abc@gmail.com")
    private String email;
}
