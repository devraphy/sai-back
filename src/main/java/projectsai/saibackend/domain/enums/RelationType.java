package projectsai.saibackend.domain.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum RelationType {
    @JsonProperty("FRIEND")
    FRIEND,
    @JsonProperty("BUSINESS")
    BUSINESS
}
