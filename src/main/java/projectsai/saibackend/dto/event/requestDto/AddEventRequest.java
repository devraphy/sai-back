package projectsai.saibackend.dto.event.requestDto;

import lombok.Data;
import projectsai.saibackend.domain.enums.EventEvaluation;
import projectsai.saibackend.domain.enums.EventPurpose;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Data @NotNull
public class AddEventRequest {
    private Long ownerId;
    private String name;
    private LocalDate date;
    private EventPurpose purpose;
    private EventEvaluation evaluation;
    private List<Long> participants;
}
