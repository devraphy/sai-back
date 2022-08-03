package projectsai.saibackend.dto.friend.requestDto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class AddFriendRequest {
    @NotNull
    private Long ownerId;
    @NotNull
    private String name;
    @NotNull
    private String relationType;
    @NotNull
    private String relationStatus;
    private String memo;
    private LocalDate birthDate;
}
