package projectsai.saibackend.dto.event.responseDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import projectsai.saibackend.domain.Event;
import projectsai.saibackend.domain.Friend;

import java.util.List;

@Data
@AllArgsConstructor
public class SearchEventResponse {
    private Event event;
    private List<Friend> participants;
}
