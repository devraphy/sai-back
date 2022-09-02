package projectsai.saibackend.dto.friend.requestDto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data @NotNull
public class DeleteFriendRequest {
    @ApiModelProperty(example = "친구 객체 PK")
    private Long friendId;
}
