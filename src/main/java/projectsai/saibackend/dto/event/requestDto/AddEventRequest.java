package projectsai.saibackend.dto.event.requestDto;

import lombok.Data;
import projectsai.saibackend.domain.enums.EventEvaluation;
import projectsai.saibackend.domain.enums.EventPurpose;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Data
public class AddEventRequest {
    @NotNull
    private Long ownerId;
    @NotNull
    private String name;
    @NotNull
    private LocalDate date;
    @NotNull
    private EventPurpose purpose;
    @NotNull
    private EventEvaluation evaluation;
    @NotNull
    private List<Long> participants;
}
