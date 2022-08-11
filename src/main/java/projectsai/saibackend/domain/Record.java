package projectsai.saibackend.domain;

import lombok.Getter;

import javax.persistence.*;
import java.util.Objects;

@Entity @Getter
public class Record {

    @Id @GeneratedValue
    @Column(name = "record_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "friend_id", nullable = false)
    private Friend friend;

    // Constructor
    public Record() {}

    public Record(Event event, Friend friend) {
        this.event = event;
        this.friend = friend;
    }

    // Equals & HashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Record that = (Record) o;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getEvent(), that.getEvent())
                && Objects.equals(getFriend(), that.getFriend());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getEvent(), getFriend());
    }
}
