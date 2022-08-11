package projectsai.saibackend.dto.friend.requestDto;

import lombok.Data;
import org.springframework.lang.Nullable;
import projectsai.saibackend.domain.enums.RelationStatus;
import projectsai.saibackend.domain.enums.RelationType;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data @NotNull
public class UpdateFriendRequest {
    private Long friendId;
    private String name;
    private RelationType type;
    private RelationStatus status;
    @Nullable
    private String memo;
    @Nullable
    private LocalDate birthDate;
}
