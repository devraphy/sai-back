package projectsai.saibackend.dto.member.requestDto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data @NotNull
public class EmailValidationRequest {
    @ApiModelProperty(example = "중복 검사할 이메일")
    private String email;
}
