package projectsai.saibackend.domain;

import lombok.Getter;

import javax.persistence.*;
import java.util.Objects;

@Entity @Getter
public class Record {

    @Id @GeneratedValue
    @Column(name = "record_id")
    private Long recordId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.EAGER)
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
        Record record = (Record) o;
        return Objects.equals(getRecordId(), record.getRecordId())
                && Objects.equals(getEvent(), record.getEvent())
                && Objects.equals(getFriend(), record.getFriend());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRecordId(), getEvent(), getFriend());
    }
}
