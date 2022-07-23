package projectsai.saibackend.repository;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import projectsai.saibackend.domain.Friend;
import projectsai.saibackend.domain.Member;
import projectsai.saibackend.domain.enums.RelationStatus;
import projectsai.saibackend.domain.enums.Relationship;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;


@RunWith(SpringRunner.class)
@SpringBootTest
public class FriendRepositoryTest {

    @Autowired MemberRepository memberRepository;
    @Autowired FriendRepository friendRepository;

// 올바르게 테스트 짰는지 점검해볼 것!
    public Member createMember() {
        Member member = new Member();
        member.setName("이근형");
        member.setEmail("abc@gmail.com");
        member.setSignUpDate(LocalDate.now());
        member.setPassword("abcdefs");
        return member;
    }

    public Friend createFriend(Member owner) {
        Friend friend = new Friend();
        friend.setName("친구1");
        friend.setOwner(owner);
        friend.setType(Relationship.FRIEND);
        friend.setStatus(RelationStatus.NORMAL);
        friend.setScore(50);
        return friend;
    }

    @Test @Transactional
    public void save() {
        // given
        Member owner = createMember();
        memberRepository.save(owner);
        Friend friend = createFriend(owner);

        // when
        Long savedFriend = friendRepository.save(friend);

        // then
        Assertions.assertThat(friend.getId()).isEqualTo(savedFriend);
    }

    @Test @Transactional
    public void findById() throws Exception {
        // given
        Member owner = createMember();
        memberRepository.save(owner);
        Friend friend = createFriend(owner);

        // when
        Long savedFriend = friendRepository.save(friend);
        Friend findFriend = friendRepository.findById(savedFriend);

        //then
        Assertions.assertThat(friend.getId()).isEqualTo(findFriend.getId());
    }

    @Test @Transactional
    public void findByName() throws Exception {
        // given
        Member owner = createMember();
        Long owner_id = memberRepository.save(owner);
        Friend friend = createFriend(owner);
        Long savedFriend = friendRepository.save(friend);

        // when
        List<Friend> friendList = friendRepository.findByName(owner, friend.getName());

        // then
        for(Friend friend1 : friendList) {
            Assertions.assertThat(friend1.getName()).isEqualTo(friend.getName());
        }
    }

    @Test @Transactional
    public void findByType() {
        // given
        Member owner = createMember();
        memberRepository.save(owner);
        Friend friend = createFriend(owner);

        // when
        Long savedFriend = friendRepository.save(friend);
        List<Friend> friendList = friendRepository.findByType(owner, Relationship.FRIEND);

        // then
        for(Friend friend1 : friendList) {
            Assertions.assertThat(friend1.getType()).isEqualTo(friend.getType());
        }
    }

    @Test @Transactional
    public void findByStatus() {
        // given
        Member owner = createMember();
        memberRepository.save(owner);
        Friend friend = createFriend(owner);

        // when
        Long savedFriend = friendRepository.save(friend);
        List<Friend> friendList = friendRepository.findByStatus(owner, RelationStatus.NORMAL);

        // then
        for(Friend friend1 : friendList) {
            Assertions.assertThat(friend1.getStatus()).isEqualTo(friend.getStatus());
        }
    }

    @Test @Transactional
    public void findAll() {
        // given
        Member owner = createMember();
        memberRepository.save(owner);
        Friend friend = createFriend(owner);

        // when
        Long savedFriend = friendRepository.save(friend);
        List<Friend> friendList = friendRepository.findAll(owner);

        // then
        for(Friend friend1 : friendList) {
            Assertions.assertThat(friend1.getOwner().getId()).isEqualTo(owner.getId());
        }
    }
}