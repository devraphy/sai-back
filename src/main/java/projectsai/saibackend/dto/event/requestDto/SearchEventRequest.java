package projectsai.saibackend.dto.event.requestDto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class SearchEventRequest {
    @NotNull
    Long ownerId;
}
