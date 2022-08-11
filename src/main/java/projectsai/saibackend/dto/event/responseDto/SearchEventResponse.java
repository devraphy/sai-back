package projectsai.saibackend.dto.event.responseDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import projectsai.saibackend.domain.Event;
import projectsai.saibackend.domain.Friend;
import projectsai.saibackend.domain.Member;
import projectsai.saibackend.domain.enums.EventEvaluation;
import projectsai.saibackend.domain.enums.EventPurpose;

import java.time.LocalDate;
import java.util.List;

@Data @AllArgsConstructor
public class SearchEventResponse {
    private Long eventId;
    private Member owner;
    private LocalDate date;
    private String name;
    private EventPurpose purpose;
    private EventEvaluation evaluation;
    private List<Friend> participants;

    public SearchEventResponse(Event event, List<Friend> participants) {
        this.eventId = event.getId();
        this.owner = event.getOwner();
        this.date = event.getDate();
        this.name = event.getName();
        this.purpose = event.getPurpose();
        this.evaluation = event.getEvaluation();
        this.participants = participants;
    }
}
