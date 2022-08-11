package projectsai.saibackend.dto.friend.requestDto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data @NotNull
public class DeleteFriendRequest {
    private Long ownerId;
    private Long friendId;
}
