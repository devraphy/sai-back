package projectsai.saibackend.dto.friend.requestDto;

import lombok.Data;
import projectsai.saibackend.domain.enums.RelationStatus;
import projectsai.saibackend.domain.enums.RelationType;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class AddFriendRequest {
    @NotNull
    private Long ownerId;
    @NotNull
    private String name;
    @NotNull
    private RelationType relationType;
    @NotNull
    private RelationStatus relationStatus;
    private String memo;
    private LocalDate birthDate;
}
