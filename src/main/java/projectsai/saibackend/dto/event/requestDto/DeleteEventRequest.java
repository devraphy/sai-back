package projectsai.saibackend.dto.event.requestDto;

import lombok.Data;

@Data
public class DeleteEventRequest {
    private Long eventId;
    private Long friendId;
}
