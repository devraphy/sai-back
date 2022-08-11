package projectsai.saibackend.dto.event.requestDto;

import lombok.Data;
import projectsai.saibackend.domain.Friend;
import projectsai.saibackend.domain.enums.EventEvaluation;
import projectsai.saibackend.domain.enums.EventPurpose;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Data @NotNull
public class UpdateEventRequest {
    private Long eventId;
    private String name;
    private LocalDate date;
    private EventPurpose purpose;
    private EventEvaluation evaluation;
    private List<Friend> participants;
}
