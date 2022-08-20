package projectsai.saibackend.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import org.springframework.lang.Nullable;
import projectsai.saibackend.domain.enums.EventEvaluation;
import projectsai.saibackend.domain.enums.RelationStatus;
import projectsai.saibackend.domain.enums.RelationType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity @Getter
public class Friend {

    @Id @GeneratedValue
    @Column(name = "friend_id")
    private Long friendId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private User owner;

    @Column(name = "friend_name", nullable = false)
    private String name;

    @NotNull
    @Enumerated(EnumType.STRING)
    private RelationType type;

    @NotNull
    @Enumerated(EnumType.STRING)
    private RelationStatus status;

    @NotNull
    private Integer score;

    @Nullable
    private String memo;

    @Nullable
    private LocalDate birthDate;

    // Constructor
    public Friend() {}

    public Friend(User owner, String name, RelationType type, RelationStatus status,
                  int score, @Nullable String memo, @Nullable LocalDate birthDate) {
        this.owner = owner;
        this.name = name;
        this.type = type;
        this.status = status;
        this.score = score;
        this.memo = memo;
        this.birthDate = birthDate;
    }

    // Business Methods
    public void setOwner(User user) {
        this.owner = user;
    }

    public void updateInfo(String name, RelationType type, Integer score, RelationStatus status, String memo, LocalDate birthDate) {
        this.name = name;
        this.type = type;
        this.score = score;
        this.status = status;
        this.memo = memo;
        this.birthDate = birthDate;
    }

    public void calcScore(EventEvaluation curnEvaluation) {
        if(curnEvaluation.equals(EventEvaluation.BAD)) this.score -= 10;
        if(curnEvaluation.equals(EventEvaluation.NEGATIVE)) this.score -= 5;
        if(curnEvaluation.equals(EventEvaluation.NORMAL)) this.score += 0;
        if(curnEvaluation.equals(EventEvaluation.POSITIVE)) this.score += 5;
        if(curnEvaluation.equals(EventEvaluation.GREAT)) this.score += 10;
    }

    public void restoreScore(EventEvaluation prevEvaluation) {
        if(prevEvaluation.equals(EventEvaluation.BAD)) this.score += 10;
        if(prevEvaluation.equals(EventEvaluation.NEGATIVE)) this.score += 5;
        if(prevEvaluation.equals(EventEvaluation.NORMAL)) this.score += 0;
        if(prevEvaluation.equals(EventEvaluation.POSITIVE)) this.score -= 5;
        if(prevEvaluation.equals(EventEvaluation.GREAT)) this.score -= 10;
    }

    public void calcStatus() {
        if(this.score <= 20) this.status = RelationStatus.BAD;
        else if(this.score <= 40) this.status = RelationStatus.NEGATIVE;
        else if(this.score <= 60) this.status = RelationStatus.NORMAL;
        else if(this.score <= 80) this.status = RelationStatus.POSITIVE;
        else this.status = RelationStatus.GREAT;
    }
}
