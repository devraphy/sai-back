package projectsai.saibackend.domain.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum EventEvaluation {
    @JsonProperty("great")
    GREAT,
    @JsonProperty("positive")
    POSITIVE,
    @JsonProperty("normal")
    NORMAL,
    @JsonProperty("negative")
    NEGATIVE,
    @JsonProperty("bad")
    BAD
}
