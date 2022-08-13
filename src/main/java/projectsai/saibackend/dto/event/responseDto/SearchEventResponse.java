package projectsai.saibackend.dto.event.responseDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import projectsai.saibackend.domain.Event;
import projectsai.saibackend.domain.Record;

import java.util.List;

@Data @AllArgsConstructor
public class SearchEventResponse {
    private Event event;
    private List<Record> eventRecords;
}
