package projectsai.saibackend.dto.friend.requestDto;

import lombok.Data;
import projectsai.saibackend.domain.enums.RelationStatus;
import projectsai.saibackend.domain.enums.RelationType;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class UpdateFriendRequest {
    @NotNull
    private Long friendId;
    @NotNull
    private String name;
    @NotNull
    private RelationType type;
    @NotNull
    private RelationStatus status;
    private String memo;
    private LocalDate birthDate;
}
