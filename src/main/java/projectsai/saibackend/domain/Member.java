package projectsai.saibackend.domain;

import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;

@Entity @Getter
public class Member implements UserDetails {
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
    private Boolean visibility;

    @NotNull
    private String authority;

    // Constructor
    public Member() {}

    public Member(String name, String email, String password, Boolean visibility, String authority) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.visibility = visibility;
        this.authority = authority;
    }

    // Business Methods
    public void updateInfo(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public void deleteMember() {
        this.visibility = Boolean.TRUE;
    }


    // UserDetails
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        ArrayList<GrantedAuthority> auth = new ArrayList<>();
        auth.add(new SimpleGrantedAuthority(authority));
        return auth;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
