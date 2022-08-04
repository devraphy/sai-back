package projectsai.saibackend.domain.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum RelationStatus {
    @JsonProperty("STRONG")
    STRONG,
    @JsonProperty("POSITIVE")
    POSITIVE,
    @JsonProperty("NORMAL")
    NORMAL,
    @JsonProperty("NEGATIVE")
    NEGATIVE,
    @JsonProperty("BAD")
    BAD
}
