package projectsai.saibackend.dto.friend.requestDto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data @NotNull
public class DeleteFriendRequest {
    @Schema(description = "삭제할 친구 ID", example = "1")
    private Long friendId;
}
