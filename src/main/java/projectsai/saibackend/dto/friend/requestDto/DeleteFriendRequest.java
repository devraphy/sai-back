package projectsai.saibackend.dto.friend.requestDto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data @NotNull
public class DeleteFriendRequest {
    @ApiModelProperty(example = "삭제할 친구 ID")
    private Long friendId;
}
