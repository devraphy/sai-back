package projectsai.saibackend.domain;

import lombok.Getter;
import org.springframework.lang.Nullable;
import projectsai.saibackend.domain.enums.RelationStatus;
import projectsai.saibackend.domain.enums.RelationType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Objects;

@Entity @Getter
public class Friend {

    @Id @GeneratedValue
    @Column(name = "friend_id")
    private Long id;

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

    @Nullable
    private LocalDate birthDate;

    public Friend() {}

    public Friend(String name, RelationType type, RelationStatus status, int score,
                  @Nullable String memo, @Nullable LocalDate birthDate) {
        this.name = name;
        this.type = type;
        this.status = status;
        this.score = score;
        this.memo = memo;
        this.birthDate = birthDate;
    }

    // 연관 관계 메서드
    public void setOwner(Member member) { // Setter 대신 사용하는 비즈니스 메서드
        this.owner = member;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Friend friend = (Friend) o;
        return getScore() == friend.getScore()
                && Objects.equals(getId(), friend.getId())
                && Objects.equals(getOwner(), friend.getOwner())
                && Objects.equals(getName(), friend.getName()) && getType() == friend.getType() && getStatus() == friend.getStatus()
                && Objects.equals(getMemo(), friend.getMemo())
                && Objects.equals(getBirthDate(), friend.getBirthDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getOwner(), getName(), getType(), getStatus(), getScore(), getMemo(), getBirthDate());
    }
}
