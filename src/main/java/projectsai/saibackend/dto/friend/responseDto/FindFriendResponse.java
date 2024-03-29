package projectsai.saibackend.dto.friend.responseDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import projectsai.saibackend.domain.Friend;
import projectsai.saibackend.domain.enums.RelationStatus;
import projectsai.saibackend.domain.enums.RelationType;



@Data
@AllArgsConstructor
public class FindFriendResponse {

    private Long id;
    private String name;
    private RelationType type;
    private RelationStatus status;
    private String memo;

    public FindFriendResponse(Friend friend) {
        this.id = friend.getFriendId();
        this.name = friend.getName();
        this.type = friend.getType();
        this.status = friend.getStatus();
        this.memo = friend.getMemo();
    }
}
