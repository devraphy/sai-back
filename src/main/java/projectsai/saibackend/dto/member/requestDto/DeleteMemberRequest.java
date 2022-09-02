package projectsai.saibackend.dto.member.requestDto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data @NotNull
public class DeleteMemberRequest {
    @ApiModelProperty(example = "삭제할 사용자 이메일")
    private String email;
}
