package projectsai.saibackend.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;
import projectsai.saibackend.domain.enums.RelationStatus;
import projectsai.saibackend.domain.enums.RelationType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity @Getter
public class Friend {

    @Id @GeneratedValue
    @Column(name = "friend_id")
    private Long id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member owner;

    @Column(name = "friend_name", nullable = false)
    private String name;

    @NotNull
    @Enumerated(EnumType.STRING)
    private RelationType type;

    @NotNull
    @Enumerated(EnumType.STRING)
    private RelationStatus status;

    @NotNull
    private int score; // 최초 친구 등록시 status에 따라서 점수가 정해짐

    @Nullable
    private String memo;

    public Friend() {}

    public Friend(String name, RelationType type, RelationStatus status, int score, @Nullable String memo) {
        this.name = name;
        this.type = type;
        this.status = status;
        this.score = score;
        this.memo = memo;
    }

    public void setOwner(Member member) {
        this.owner = member;
    }
}
