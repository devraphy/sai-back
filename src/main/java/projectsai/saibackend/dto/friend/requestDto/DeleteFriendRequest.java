package projectsai.saibackend.dto.friend.requestDto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class DeleteFriendRequest {

    @NotNull
    private Long ownerId;
    @NotNull
    private Long friendId;
}
