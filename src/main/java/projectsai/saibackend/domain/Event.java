package projectsai.saibackend.domain;

import lombok.Getter;
import org.springframework.lang.Nullable;
import projectsai.saibackend.domain.enums.EventEvaluation;
import projectsai.saibackend.domain.enums.EventPurpose;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity @Getter
public class Event {

    @Id @GeneratedValue
    @Column(name = "event_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member owner;

    @NotNull
    private LocalDate eventDate;

    @Enumerated(EnumType.STRING) @NotNull
    private EventPurpose eventPurpose;

    @Column(name = "event_name") @Nullable
    private String eventName;

    @Enumerated(EnumType.STRING) @NotNull
    private EventEvaluation evaluation;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "event_participants",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id"))
    private List<Friend> participants = new ArrayList<>();

    // 연관 관계 메서드
    public void setOwner(Member member) {
        this.owner = member;
    }

    public Event() {}

    public Event(LocalDate eventDate, EventPurpose eventPurpose, @Nullable String eventName,
                 EventEvaluation evaluation, List<Friend> participants) {
        this.eventDate = eventDate;
        this.eventPurpose = eventPurpose;
        this.eventName = eventName;
        this.evaluation = evaluation;
        this.participants = participants;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return Objects.equals(getId(), event.getId())
                && Objects.equals(getOwner(), event.getOwner())
                && Objects.equals(getEventDate(), event.getEventDate()) && getEventPurpose() == event.getEventPurpose()
                && Objects.equals(getEventName(), event.getEventName()) && getEvaluation() == event.getEvaluation()
                && Objects.equals(getParticipants(), event.getParticipants());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getOwner(), getEventDate(), getEventPurpose(), getEventName(), getEvaluation(), getParticipants());
    }
}
