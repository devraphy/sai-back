package projectsai.saibackend.dto.event.requestDto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import projectsai.saibackend.domain.enums.EventEvaluation;
import projectsai.saibackend.domain.enums.EventPurpose;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Data @NotNull
public class AddEventRequest {
    @ApiModelProperty(example = "이벤트 이름")
    private String name;

    @ApiModelProperty(example = "이벤트 날짜")
    private LocalDate date;

    @ApiModelProperty(example = "이벤트 목적(chill, work)")
    private EventPurpose purpose;

    @ApiModelProperty(example = "이벤트 평가(great, positive, normal, negative, bad)")
    private EventEvaluation evaluation;

    @ApiModelProperty(example = "이벤트 참가자 리스트(Long)")
    private List<Long> participants;
}
