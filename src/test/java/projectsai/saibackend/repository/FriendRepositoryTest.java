package projectsai.saibackend.repository;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import projectsai.saibackend.domain.Friend;
import projectsai.saibackend.domain.Member;
import projectsai.saibackend.domain.enums.RelationStatus;
import projectsai.saibackend.domain.enums.RelationType;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Transactional
@SpringBootTest @Slf4j
public class FriendRepositoryTest {

    @PersistenceContext EntityManager em;
    @Autowired FriendRepository friendRepository;

    private Friend friend1, friend2, friend3;
    private Member owner;

    @BeforeEach
    public void createMemberAndFriend() {
        owner = new Member("이근형","abc@gmail.com", "abcde", LocalDate.now(), Boolean.TRUE);
        friend1 = new Friend("친구1", RelationType.FRIEND, RelationStatus.NORMAL, 50, null, null);
        friend2 = new Friend("친구2", RelationType.FRIEND, RelationStatus.POSITIVE, 80, null, null);
        friend3 = new Friend("친구3", RelationType.BUSINESS, RelationStatus.NORMAL, 50, null, null);
        owner.addFriend(friend1);
        owner.addFriend(friend2);
        owner.addFriend(friend3);
        em.persist(owner);
    }

    @Test @DisplayName("Friend - 친구 저장")
    public void save() throws Exception {
        // given
        Friend newFriend = new Friend(owner, "테스트친구", RelationType.FRIEND, RelationStatus.NORMAL, 50, null, null);


        // when
        Long savedFriendId = friendRepository.save(newFriend);

        // then
        Assertions.assertThat(savedFriendId).isEqualTo(newFriend.getId());
    }

    @Test @DisplayName("Friend - 전체 검색")
    public void findAll() throws Exception {
        // given

        // when
        List<Friend> friendList = friendRepository.findAll(owner.getId());

        // then
        for(Friend friend1 : friendList) {
            log.info("ID: " + friend1.getId() + " " + friend1.getName());
            Assertions.assertThat(friend1.getOwner().getId()).isEqualTo(owner.getId());
        }
    }

    @Test @DisplayName("Friend - Id 검색")
    public void findById() throws Exception {
       //given

       //when
        Friend findFriend = friendRepository.findById(friend1.getId());

        //then
        Assertions.assertThat(findFriend.getId()).isEqualTo(friend1.getId());
    }

    @Test @DisplayName("Friend - 이름으로 검색")
    public void findByName() throws Exception {
        // given
        String name = "친구1";

        // when
        List<Friend> friendList = friendRepository.findByName(owner.getId(), name);

        // then
        for(Friend friend1 : friendList) {
            log.info("이름: " + friend1.getName());
            Assertions.assertThat(friend1.getName()).isEqualTo(name);
        }
    }

    @Test @DisplayName("Friend - 관계 종류로 검색")
    public void findByType() throws Exception {
        // given
        RelationType type = RelationType.FRIEND;

        // when
        List<Friend> friendList = friendRepository.findByType(owner.getId(), type);

        // then
        for(Friend friend1 : friendList) {
            log.info("이름: " + friend1.getName() + " / type =>" + friend1.getType());
            Assertions.assertThat(friend1.getType()).isEqualTo(type);
        }
    }

    @Test @DisplayName("Friend - 관계 상태로 검색")
    public void findByStatus() throws Exception {
        // given
        RelationStatus status = RelationStatus.NORMAL;

        // when
        List<Friend> friendList = friendRepository.findByStatus(owner.getId(), status);

        // then
        for(Friend friend1 : friendList) {
            log.info("이름: " + friend1.getName() + " / status => " + friend1.getStatus());
            Assertions.assertThat(friend1.getStatus()).isEqualTo(status);
        }
    }

    @Test @DisplayName("Friend - 다중 ID 검색")
    public void findFriends() throws Exception {
        //given
        List<Long> friendIds = new ArrayList<>();
        friendIds.add(friend1.getId());
        friendIds.add(friend2.getId());
        friendIds.add(friend3.getId());

        //when
        List<Friend> friends = friendRepository.findFriends(owner.getId(), friendIds);

        //then
        for(Friend one : friends) {
            Assertions.assertThat(one.getName()).isIn("친구1", "친구2", "친구3");
        }
    }

}