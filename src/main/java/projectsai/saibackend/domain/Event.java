package projectsai.saibackend.domain;

import lombok.Getter;
import lombok.Setter;
import projectsai.saibackend.domain.enums.Evaluation;
import projectsai.saibackend.domain.enums.EventPurpose;

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
    @JoinColumn(name = "member_id")
    private Member owner;

    private LocalDate eventDate;

    @Enumerated(EnumType.STRING)
    private EventPurpose eventPurpose;

    @Column(name = "event_name")
    private String eventName;

    @Enumerated(EnumType.STRING)
    private Evaluation evaluation;

    // 이거 돌려보고 DB 구조 확인해볼 것
    @ManyToMany
    @JoinTable(name = "event_participants")
    private List<Friend> participants = new ArrayList<>();
}
