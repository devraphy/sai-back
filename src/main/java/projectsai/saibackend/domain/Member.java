package projectsai.saibackend.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import projectsai.saibackend.dto.member.requestDto.JoinMemberRequest;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Entity @Getter @NoArgsConstructor(access = AccessLevel.PROTECTED)
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
    private Boolean visibility;

    @ManyToMany
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "member_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private List<Role> roles = new ArrayList<>();

    // Constructor

    @Builder
    public Member(String name, String email, String password, Boolean visibility, List<Role> roles) {
        this.name = name;
        this.email = email.toLowerCase();
        this.password = password;
        this.visibility = visibility;
        this.roles = roles;
    }

    public static Member buildMember(JoinMemberRequest joinMemberRequest, PasswordEncoder passwordEncoder) {
        Member member = Member.builder()
                .name(joinMemberRequest.getName())
                .email(joinMemberRequest.getEmail().toLowerCase())
                .password(passwordEncoder.encode(joinMemberRequest.getPassword()))
                .visibility(Boolean.TRUE)
                .build();
        return member;
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
