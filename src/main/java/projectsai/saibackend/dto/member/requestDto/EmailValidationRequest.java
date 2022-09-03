package projectsai.saibackend.dto.member.requestDto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@NotNull
public class EmailValidationRequest {
    @Schema(description = "중복 검사할 이메일", example = "abc@gmail.com")
    private String email;
}
