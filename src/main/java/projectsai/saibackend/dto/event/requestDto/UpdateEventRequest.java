package projectsai.saibackend.dto.event.requestDto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import projectsai.saibackend.domain.enums.EventEvaluation;
import projectsai.saibackend.domain.enums.EventPurpose;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Data @NotNull
public class UpdateEventRequest {
    @Schema(description = "이벤트 ID", example = "1")
    private Long eventId;

    @Schema(description = "변경할 이벤트 이름", example = "출사 가는날")
    private String name;

    @Schema(description = "변경할 이벤트 날짜", example = "2022-09-03")
    private LocalDate date;

    @Schema(description = "변경할 이벤트 목적", example = "chill, work 중 하나(String)")
    private EventPurpose purpose;

    @Schema(description = "변경할 이벤트 평가", example = "great, positive, normal, negative, bad 중 하나(String)")
    private EventEvaluation evaluation;

    @Schema(description = "변경할 이벤트 참가자 리스트", example = "[1,2,3,4,5]")
    private List<Long> participants;
}
