package projectsai.saibackend.domain.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum RelationType {
    @JsonProperty("friend")
    FRIEND,
    @JsonProperty("business")
    BUSINESS
}
