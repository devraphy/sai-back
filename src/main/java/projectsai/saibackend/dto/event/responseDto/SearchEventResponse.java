package projectsai.saibackend.dto.event.responseDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import projectsai.saibackend.domain.Event;
import projectsai.saibackend.domain.Friend;
import projectsai.saibackend.domain.enums.EventEvaluation;
import projectsai.saibackend.domain.enums.EventPurpose;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
public class SearchEventResponse {
    private Long eventId;
    private String eventName;
    private LocalDate date;
    private EventPurpose purpose;
    private EventEvaluation evaluation;
    private List<Friend> participants;
}
