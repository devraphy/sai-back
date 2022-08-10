package projectsai.saibackend.domain;

import lombok.Getter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Objects;

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

    @NotNull
    private LocalDate signUpDate;

    @NotNull
    private Boolean visibility;

    // Constructor
    public Member() {}

    public Member(String name, String email, String password, LocalDate signUpDate, Boolean visibility) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.signUpDate = signUpDate;
        this.visibility = visibility;
    }

    // Equals & HashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Member member = (Member) o;
        return Objects.equals(getId(), member.getId()) && Objects.equals(getName(), member.getName())
                && Objects.equals(getEmail(), member.getEmail())
                && Objects.equals(getPassword(), member.getPassword())
                && Objects.equals(getSignUpDate(), member.getSignUpDate())
                && Objects.equals(getVisibility(), member.getVisibility());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getEmail(), getPassword(), getSignUpDate(), getVisibility());
    }

    // Setter 대신 사용하는 비즈니스 메서드
    public void updateInfo(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public void deleteMember() {
        this.visibility = Boolean.FALSE;
    }

}
