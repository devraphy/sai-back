package projectsai.saibackend.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity @Getter @Setter
public class Event {

    @Id @GeneratedValue
    @Column(name = "event_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User owner;

    private LocalDate eventDate;

    @Enumerated(EnumType.STRING)
    private EventPurpose eventPurpose;

    @Column(name = "event_name")
    private String eventName;

    @Enumerated(EnumType.STRING)
    private Evaluation evaluation;

    // OneToMany 이렇게 하는거 맞나? 김영한 강의 다시 찾아볼 것
    @OneToMany
    @JoinColumn(name = "friend_id")
    private List<Friend> participants = new ArrayList<>();
}
