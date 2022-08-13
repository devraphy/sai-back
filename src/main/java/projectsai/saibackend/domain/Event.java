package projectsai.saibackend.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import projectsai.saibackend.domain.enums.EventEvaluation;
import projectsai.saibackend.domain.enums.EventPurpose;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Objects;

@Entity @Getter
public class Event {

    @Id @GeneratedValue
    @Column(name = "event_id")
    private Long eventId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member owner;

    @NotNull
    private LocalDate date;

    @Enumerated(EnumType.STRING) @NotNull
    private EventPurpose purpose;

    @Column(name = "event_name") @NotNull
    private String name;

    @Enumerated(EnumType.STRING) @NotNull
    private EventEvaluation evaluation;

    // Constructor
    public Event() {}

    public Event(Member owner, LocalDate date, EventPurpose purpose, String name, EventEvaluation evaluation) {
        this.owner = owner;
        this.date = date;
        this.purpose = purpose;
        this.name = name;
        this.evaluation = evaluation;
    }

    // Equals & HashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return Objects.equals(getEventId(), event.getEventId())
                & Objects.equals(getOwner(), event.getOwner())
                && Objects.equals(getDate(), event.getDate())
                && getPurpose() == event.getPurpose()
                && Objects.equals(getName(), event.getName()) && getEvaluation() == event.getEvaluation();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEventId(), getOwner(), getDate(), getPurpose(), getName(), getEvaluation());
    }

    // Business Methods
    public void updateInfo(String name, LocalDate date, EventPurpose purpose, EventEvaluation evaluation){
        this.name = name;
        this.date = date;
        this.purpose = purpose;
        this.evaluation = evaluation;
    }
}
