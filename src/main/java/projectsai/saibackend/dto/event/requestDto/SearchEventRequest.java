package projectsai.saibackend.dto.event.requestDto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data @NotNull
public class SearchEventRequest {
    private Long ownerId;
}
