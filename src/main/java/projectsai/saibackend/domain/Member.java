package projectsai.saibackend.domain;

import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity @Getter
public class Member {
    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    @Column(name = "member_name", nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private LocalDate signUpDate;

    public Member() {}

    public Member(Long id, String name, String email, String password, LocalDate signUpDate) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.signUpDate = signUpDate;
    }
}
