package projectsai.saibackend.dto.event.requestDto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import projectsai.saibackend.domain.enums.EventEvaluation;
import projectsai.saibackend.domain.enums.EventPurpose;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Data
@NotNull
public class AddEventRequest {
    @Schema(description = "이벤트 이름", example = "곽두팔 생일파티")
    private String name;

    @Schema(description = "이벤트 날짜", example = "2022-09-03")
    private LocalDate date;

    @Schema(description = "이벤트 목적", example = "chill, work 중 하나(String)")
    private EventPurpose purpose;

    @Schema(description = "이벤트 평가", example = "great, positive, normal, negative, bad 중 하나(String)")
    private EventEvaluation evaluation;

    @Schema(description = "이벤트 참가자 리스트(Long)", example = "[1,2,3,4,5]")
    private List<Long> participants;
}
