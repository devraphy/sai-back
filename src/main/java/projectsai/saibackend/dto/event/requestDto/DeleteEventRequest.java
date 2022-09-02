package projectsai.saibackend.dto.event.requestDto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data @NotNull
public class DeleteEventRequest {
    @ApiModelProperty(example = "삭제할 이벤트 ID")
    private Long eventId;
}
