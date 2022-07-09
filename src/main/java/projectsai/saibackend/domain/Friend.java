package projectsai.saibackend.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity @Getter @Setter
public class Friend {

    @Id @GeneratedValue
    @Column(name = "friend_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User owner;

    @Column(name = "friend_name")
    private String name;

    @Enumerated(EnumType.STRING)
    private Relationship relationship;

    @Enumerated(EnumType.STRING)
    private RelationStatus relationStatus;

    private LocalDate birthDate;
    private LocalDate lastContact;
    private String memo;
}
