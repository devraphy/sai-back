package projectsai.saibackend.dto.event.requestDto;

import lombok.Data;
import projectsai.saibackend.domain.Friend;
import projectsai.saibackend.domain.enums.EventEvaluation;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Data
public class UpdateEventRequest {
    @NotNull
    Long eventId;
    @NotNull
    String name;
    @NotNull
    LocalDate date;
    @NotNull
    EventEvaluation evaluation;
    @NotNull
    List<Friend> participants;
}
