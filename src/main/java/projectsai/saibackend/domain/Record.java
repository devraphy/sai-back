package projectsai.saibackend.domain;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class Record {

    @Id
    @GeneratedValue
    @Column(name = "record_id")
    private Long recordId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "friend_id", nullable = false)
    private Friend friend;

    // Constructor
    public Record() {
    }

    public Record(Event event, Friend friend) {
        this.event = event;
        this.friend = friend;
    }
}
