package projectsai.saibackend.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity @Getter
@NoArgsConstructor
public class Role {
    @Id @GeneratedValue
    @Column(name = "role_id")
    private Long id;
    private String position;

    public Role(String position) {
        this.position = position;
    }
}
