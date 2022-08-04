package projectsai.saibackend.domain.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum EventEvaluation {
    @JsonProperty("GREAT")
    GREAT,
    @JsonProperty("POSITIVE")
    POSITIVE,
    @JsonProperty("NORMAL")
    NORMAL,
    @JsonProperty("NEGATIVE")
    NEGATIVE,
    @JsonProperty("BAD")
    BAD
}
