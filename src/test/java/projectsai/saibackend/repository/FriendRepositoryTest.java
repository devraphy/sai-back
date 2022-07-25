package projectsai.saibackend.repository;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import projectsai.saibackend.domain.Friend;
import projectsai.saibackend.domain.Member;
import projectsai.saibackend.domain.enums.RelationStatus;
import projectsai.saibackend.domain.enums.RelationType;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;


@RunWith(SpringRunner.class)
@SpringBootTest
public class FriendRepositoryTest {

    @Autowired FriendRepository friendRepository;
    @PersistenceContext EntityManager em;

    private Friend friend;
    private Member owner;

    @BeforeEach @Rollback(value = false)
    public void createMemberAndFriend() {
        owner = new Member("이근형","abc@gmail.com", "abcde", LocalDate.now());
        friend = new Friend("친구1", RelationType.FRIEND, RelationStatus.NORMAL, 50, "test");
        owner.addFriend(friend);
        em.persist(owner);
    }

    @Test @Transactional @DisplayName("친구 - ID 검색")
    public void findById() throws Exception {
        // given
//        Long savedFriendId = friendRepository.save(friend);

        // when
        Friend findFriend = friendRepository.findById(friend.getId());
        // 여기서 NullPointer 에러 난다. ==> 우선 DB에 제대로 친구가 등록되는지 확인해보자.

        //then
        Assertions.assertThat(friend.getId()).isEqualTo(findFriend.getId());
    }

    @Test @Transactional @DisplayName("친구 - 이름 검색")
    public void findByName() throws Exception {
        // given
//        Long savedFriend = friendRepository.save(friend);

        // when
        List<Friend> friendList = friendRepository.findByName(owner, friend.getName());

        // then
        for(Friend friend1 : friendList) {
            Assertions.assertThat(friend1.getName()).isEqualTo(friend.getName());
        }
    }

    @Test @Transactional @DisplayName("친구 - 관계 종류 검색")
    public void findByType() {
        // given
//        Long savedFriend = friendRepository.save(friend);

        // when
        List<Friend> friendList = friendRepository.findByType(owner, RelationType.FRIEND);

        // then
        for(Friend friend1 : friendList) {
            Assertions.assertThat(friend1.getType()).isEqualTo(friend.getType());
        }
    }

    @Test @Transactional @DisplayName("친구 - 관계 상태 검색")
    public void findByStatus() {
        // given
//        Long savedFriend = friendRepository.save(friend);

        // when
        List<Friend> friendList = friendRepository.findByStatus(owner, RelationStatus.NORMAL);

        // then
        for(Friend friend1 : friendList) {
            Assertions.assertThat(friend1.getStatus()).isEqualTo(friend.getStatus());
        }
    }

    @Test @Transactional @DisplayName("친구 - 모든 친구 검색")
    public void findAll() {
        // given
//        Long savedFriend = friendRepository.save(friend);

        // when
        List<Friend> friendList = friendRepository.findAll(owner);

        // then
        for(Friend friend1 : friendList) {
            Assertions.assertThat(friend1.getOwner().getId()).isEqualTo(owner.getId());
        }
    }
}