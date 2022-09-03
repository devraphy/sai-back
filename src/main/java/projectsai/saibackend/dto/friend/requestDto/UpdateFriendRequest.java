package projectsai.saibackend.dto.friend.requestDto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.lang.Nullable;
import projectsai.saibackend.domain.enums.RelationStatus;
import projectsai.saibackend.domain.enums.RelationType;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data @NotNull
public class UpdateFriendRequest {
    @Schema(description = "친구 ID", example = "1")
    private Long friendId;

    @Schema(description = "변경할 친구 이름", example = "곽두팔")
    private String name;

    @Schema(description = "변경할 관계 종류", example = "friend, business 중 하나(String)")
    private RelationType type;

    @Schema(description = "변경할 관계 상태", example = "great, positive, normal, negative, bad 중 하나(String)")
    private RelationStatus status;

    @Nullable
    @Schema(description = "변경할 추가 정보", example = "대학 동기")
    private String memo;
}
