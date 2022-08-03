package projectsai.saibackend.dto.friend.responseDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import projectsai.saibackend.domain.Friend;
import projectsai.saibackend.domain.enums.RelationStatus;
import projectsai.saibackend.domain.enums.RelationType;

import java.time.LocalDate;


@Data @AllArgsConstructor
public class SearchFriendResponse {

    private Long id;
    private String name;
    private RelationType type;
    private RelationStatus status;
    private String memo;
    private LocalDate birthDate;

    public SearchFriendResponse(Friend friend) {
        this.id = friend.getId();
        this.name = friend.getName();
        this.type = friend.getType();
        this.status = friend.getStatus();
        this.memo = friend.getMemo();
        this.birthDate = friend.getBirthDate();
    }
}
