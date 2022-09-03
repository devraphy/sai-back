package projectsai.saibackend.dto.event.requestDto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@NotNull
public class DeleteEventRequest {
    @Schema(description = "삭제할 이벤트 ID", example = "abc@gmail.com")
    private Long eventId;
}
