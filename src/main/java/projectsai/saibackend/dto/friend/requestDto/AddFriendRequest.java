package projectsai.saibackend.dto.friend.requestDto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.lang.Nullable;
import projectsai.saibackend.domain.enums.RelationStatus;
import projectsai.saibackend.domain.enums.RelationType;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data @NotNull
public class AddFriendRequest {
    @ApiModelProperty(example = "친구 이름")
    private String name;

    @ApiModelProperty(example = "관계 종류(friend, business)")
    private RelationType type;

    @ApiModelProperty(example = "관계 상태(great, positive, normal, negative, bad)")
    private RelationStatus status;

    @Nullable
    @ApiModelProperty(example = "추가 정보")
    private String memo;

    @Nullable
    @ApiModelProperty(example = "친구 생일")
    private LocalDate birthDate;
}
