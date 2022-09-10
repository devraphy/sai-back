package projectsai.saibackend.domain;

import lombok.Getter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Getter
public class Member {
    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long memberId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @NotNull
    private LocalDateTime signUpDate;

    @NotNull
    private Boolean visibility;

    @NotNull
    private String role;

    // Constructor
    public Member() {
    }

    public Member(String name, String email, String password, Boolean visibility, String role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.signUpDate = LocalDateTime.now();
        this.visibility = visibility;
        this.role = role;
    }

    // Business Methods
    public void updateInfo(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public void deleteMember() {
        this.visibility = Boolean.FALSE;
    }
}
