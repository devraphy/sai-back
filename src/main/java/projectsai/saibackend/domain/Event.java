package projectsai.saibackend.domain;

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
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "friend_id", nullable = false)
    private Friend friend;

    @NotNull
    private LocalDate date;

    @Enumerated(EnumType.STRING) @NotNull
    private EventPurpose purpose;

    @Column(name = "event_name") @NotNull
    private String name;

    @Enumerated(EnumType.STRING) @NotNull
    private EventEvaluation evaluation;

    public Event() {}

    public Event(Member owner, LocalDate date, EventPurpose purpose, String name, EventEvaluation evaluation, Friend friend) {
        this.owner = owner;
        this.date = date;
        this.purpose = purpose;
        this.name = name;
        this.evaluation = evaluation;
        this.friend = friend;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return Objects.equals(getId(), event.getId())
                && Objects.equals(getOwner(), event.getOwner())
                && Objects.equals(getDate(), event.getDate()) && getPurpose() == event.getPurpose()
                && Objects.equals(getName(), event.getName()) && getEvaluation() == event.getEvaluation();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getOwner(), getDate(), getPurpose(), getName(), getEvaluation());
    }

    // Setter 대신 사용하는 비즈니스 메서드
    public void setOwner(Member member) {
        this.owner = member;
    }

    public void setParticipants(Friend friend) {
        this.friend = friend;
    }
}
