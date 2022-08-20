package projectsai.saibackend.domain;

import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.crypto.password.PasswordEncoder;
import projectsai.saibackend.dto.member.requestDto.JoinMemberRequest;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity @Getter
public class Member {
    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long memberId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @CreationTimestamp
    private Timestamp signUpDate;

    @NotNull
    private Integer visibility;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(joinColumns = @JoinColumn(name = "member_id"))
    private List<Role> roles = new ArrayList<>();

    // Constructor
    public Member() {}

    @Builder
    public Member(String name, String email, String password, Integer visibility, List<Role> roles) {
        this.name = name;
        this.email = email.toLowerCase();
        this.password = password;
        this.visibility = visibility;
        this.roles = roles;
    }

    public static Member buildMember(JoinMemberRequest joinMemberRequest, PasswordEncoder passwordEncoder) {
        return Member.builder()
                .name(joinMemberRequest.getName())
                .email(joinMemberRequest.getEmail().toLowerCase())
                .password(passwordEncoder.encode(joinMemberRequest.getPassword()))
                .visibility(1)
                .roles(new ArrayList<>())
                .build();
    }

    // Business Methods
    public void updateInfo(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public void addRoleToUser(Role role) {
        this.roles.add(role);
    }

    public void deleteMember() {
        this.visibility = 0;
    }
}
