package projectsai.saibackend.domain.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum EventPurpose {
    @JsonProperty("work")
    WORK,
    @JsonProperty("chill")
    CHILL
}
