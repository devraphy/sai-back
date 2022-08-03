package projectsai.saibackend.dto.friend.requestDto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class SearchFriendRequest {
    @NotNull
    private Long ownerId;
}
