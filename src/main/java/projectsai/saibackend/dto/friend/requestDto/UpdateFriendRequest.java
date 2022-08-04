package projectsai.saibackend.dto.friend.requestDto;

import lombok.Data;
import projectsai.saibackend.domain.enums.RelationType;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class UpdateFriendRequest {
    @NotNull
    private Long ownerId;
    @NotNull
    private Long friendId;
    @NotNull
    private String name;
    private String memo;
    private LocalDate birthDate;
    @NotNull
    private RelationType relationType;
}