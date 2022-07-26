package projectsai.saibackend.domain;

import lombok.Getter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    // Member 객체를 생성하고 그 멤버 객체의 addFriend 기능을 이용해서 Friend를 저장해야한다.
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private List<Friend> friendList = new ArrayList<>();

    public void addFriend(Friend friend) {
        this.friendList.add(friend);
        friend.setOwner(this);
    }

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private List<Event> eventList = new ArrayList<>();

    public void addEvent(Event event) {
        this.eventList.add(event);
        event.setOwner(this);
    }

    public Member() {}

    public Member(String name, String email, String password, LocalDate signUpDate) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.signUpDate = signUpDate;
    }
}
