package projectsai.saibackend.dto.event.responseDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import projectsai.saibackend.domain.Event;
import projectsai.saibackend.domain.Friend;
import projectsai.saibackend.domain.enums.EventEvaluation;

import java.time.LocalDate;
import java.util.List;

@Data @AllArgsConstructor
public class SearchEventResponse {
    private Long eventId;
    private Long ownerId;
    private LocalDate date;
    private String name;
    private EventEvaluation evaluation;
    private List<Friend> participants;

    public SearchEventResponse(Event event) {
        this.eventId = event.getId();
        this.ownerId = event.getOwner().getId();
        this.date = event.getDate();
        this.name = event.getName();
        this.evaluation = event.getEvaluation();
        this.participants = event.getParticipants();
    }
}
