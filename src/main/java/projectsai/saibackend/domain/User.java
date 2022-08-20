package projectsai.saibackend.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.crypto.password.PasswordEncoder;
import projectsai.saibackend.dto.member.requestDto.JoinMemberRequest;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Entity @Getter
@Table(name = "USERS")
public class User {
    @Id @GeneratedValue
    @Column(name = "user_id")
    private Long userId;

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

    // Constructor

    public User() {}

    @Builder
    public User(String name, String email, String password, Integer visibility) {
        this.name = name;
        this.email = email.toLowerCase();
        this.password = password;
        this.visibility = visibility;
    }

    public static User buildMember(JoinMemberRequest joinMemberRequest, PasswordEncoder passwordEncoder) {
        return User.builder()
                .name(joinMemberRequest.getName())
                .email(joinMemberRequest.getEmail().toLowerCase())
                .password(passwordEncoder.encode(joinMemberRequest.getPassword()))
                .visibility(1)
                .build();
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
