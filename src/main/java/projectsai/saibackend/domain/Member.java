package projectsai.saibackend.domain;

import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.crypto.password.PasswordEncoder;
import projectsai.saibackend.dto.member.requestDto.JoinMemberRequest;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Objects;

@Entity @Getter
public class Member {
    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "member_name", nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @CreationTimestamp
    private LocalDate signUpDate;

    @NotNull
    private Integer visibility;

    // Constructor
    public Member() {}

    @Builder
    public Member(String name, String email, String password, Integer visibility) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.visibility = visibility;
    }

    public static Member buildMember(JoinMemberRequest joinMemberRequest, PasswordEncoder passwordEncoder) {
        Member member = Member.builder()
                .name(joinMemberRequest.getName())
                .email(joinMemberRequest.getEmail())
                .password(passwordEncoder.encode(joinMemberRequest.getPassword()))
                .visibility(1)
                .build();
        return member;
    }

    // Equals & HashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Member member = (Member) o;
        return Objects.equals(getMemberId(), member.getMemberId())
                && Objects.equals(getName(), member.getName())
                && Objects.equals(getEmail(), member.getEmail())
                && Objects.equals(getPassword(), member.getPassword())
                && Objects.equals(getSignUpDate(), member.getSignUpDate())
                && Objects.equals(getVisibility(), member.getVisibility());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMemberId(), getName(), getEmail(), getPassword(), getSignUpDate(), getVisibility());
    }

    // Business Methods
    public void updateInfo(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public void deleteMember() {
        this.visibility = 0;
    }

}
