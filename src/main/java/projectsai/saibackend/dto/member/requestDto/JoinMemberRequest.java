package projectsai.saibackend.dto.member.requestDto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data @NotNull
public class JoinMemberRequest {
    @ApiModelProperty(example = "이메일")
    private String email;
    @ApiModelProperty(example = "사용자 이름")
    private String name;
    @ApiModelProperty(example = "비밀번호")
    private String password;
}
