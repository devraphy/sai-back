package projectsai.saibackend.domain;

import lombok.Getter;
import lombok.Setter;
import projectsai.saibackend.domain.enums.RelationStatus;
import projectsai.saibackend.domain.enums.Relationship;

import javax.persistence.*;
import java.time.LocalDate;

@Entity @Getter @Setter
public class Friend {

    @Id @GeneratedValue
    @Column(name = "friend_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member owner;

    @Column(name = "friend_name")
    private String name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Relationship type;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RelationStatus status;

    private Long score; // 최초 친구 등록시 status에 따라서 점수가 정해짐
    private LocalDate birthDate;
    private LocalDate lastContact;
    private String memo;
}
