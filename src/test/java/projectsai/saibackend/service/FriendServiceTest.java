package projectsai.saibackend.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
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
import projectsai.saibackend.repository.FriendRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional @Slf4j
class FriendServiceTest {

    @PersistenceContext EntityManager em;
    @Autowired FriendRepository friendRepository;

    private Member owner;

    @BeforeEach
    private void createMember() {
        owner = new Member("라파파", "rapapa@gmail.com", "abcde", LocalDate.now(), Boolean.TRUE);
        em.persist(owner);
    }

    @Test @DisplayName("Friend - 친구 등록")
    void add() {
        // given
        Friend friend1 = new Friend("친구1", RelationType.FRIEND, RelationStatus.NORMAL, 50, null, null, null);

        // when
        Long savedFriendId = friendRepository.save(owner, friend1);

        // then
        Assertions.assertEquals(friend1, friendRepository.findById(owner.getId(), savedFriendId));
    }

    @Test @DisplayName("Friend - 모든 친구 검색")
    void findAll() {
        // given
        Friend friend1 = new Friend("친구1", RelationType.FRIEND, RelationStatus.NORMAL, 50, null, null, null);
        Friend friend2 = new Friend("친구2", RelationType.FRIEND, RelationStatus.NORMAL, 50, null, null, null);
        Friend friend3 = new Friend("친구3", RelationType.FRIEND, RelationStatus.NORMAL, 50, null, null, null);
        Friend business1 = new Friend("동료1", RelationType.BUSINESS, RelationStatus.NORMAL, 50, null, null, null);
        Friend business2 = new Friend("동료2", RelationType.BUSINESS, RelationStatus.NORMAL, 50, null, null, null);
        Friend business3 = new Friend("동료3", RelationType.BUSINESS, RelationStatus.NORMAL, 50, null, null, null);

        List<Friend> friendList = new ArrayList<>();
        friendList.add(friend1);
        friendList.add(friend2);
        friendList.add(friend3);
        friendList.add(business1);
        friendList.add(business2);
        friendList.add(business3);

        for(Friend friend : friendList) {
            friendRepository.save(owner, friend);
        }

        // when
        List<Friend> allFriends = friendRepository.findAll(owner.getId());

        // then
        for(Friend friend : allFriends) {
            org.assertj.core.api.Assertions.assertThat(friend).isIn(friendList);
        }
    }

    @Test @DisplayName("Friend - ID로 검색")
    void findById() {
        // given
        Friend friend1 = new Friend("친구1", RelationType.FRIEND, RelationStatus.NORMAL, 50, null, null, null);
        Long savedFriendId = friendRepository.save(owner, friend1);

        // when
        Friend findFriend = friendRepository.findById(owner.getId(), savedFriendId);

        // then
        Assertions.assertEquals(findFriend, friend1);
    }

    @Test @DisplayName("Friend - 이름으로 검색")
    void findByName() {
        // given
        Friend friend1 = new Friend("친구1", RelationType.FRIEND, RelationStatus.NORMAL, 50, null, null, null);
        Friend friend2 = new Friend("친구2", RelationType.FRIEND, RelationStatus.NORMAL, 50, null, null, null);
        Long savedFriendId1 = friendRepository.save(owner, friend1);
        Long savedFriendId2 = friendRepository.save(owner, friend2);

        // when
        List<Friend> findFriendList = friendRepository.findByName(owner.getId(), "친구1");

        // then
        for(Friend friend : findFriendList) {
            Assertions.assertEquals(friend.getName(), friend1.getName());
        }
    }

    @Test @DisplayName("Friend - 관계 종류로 검색")
    void findByType() {
        // given
        Friend friend1 = new Friend("친구1", RelationType.FRIEND, RelationStatus.NORMAL, 50, null, null, null);
        Long savedFriendId1 = friendRepository.save(owner, friend1);

        // when
        List<Friend> findFriendList = friendRepository.findByType(owner.getId(), RelationType.FRIEND);

        // then
        for(Friend friend : findFriendList) {
            Assertions.assertEquals(friend.getType(), friend1.getType());
        }
    }

    @Test @DisplayName("Friend - 관계 상태로 검색")
    void findByStatus() {
        // given
        Friend friend1 = new Friend("친구1", RelationType.FRIEND, RelationStatus.NORMAL, 50, null, null, null);
        Long savedFriendId1 = friendRepository.save(owner, friend1);

        // when
        List<Friend> findFriendList = friendRepository.findByStatus(owner.getId(), RelationStatus.NORMAL);

        // then
        for(Friend friend : findFriendList) {
            Assertions.assertEquals(friend.getStatus(), friend1.getStatus());
        }
    }

    @Test @DisplayName("Friend - 친구 정보 수정")
    void updateById() {
        // given
        Friend friend1 = new Friend("친구1", RelationType.FRIEND, RelationStatus.NORMAL, 50, null, null, null);
        Long savedFriendId1 = friendRepository.save(owner, friend1);

        // when
        int result = friendRepository.updateById(owner.getId(), savedFriendId1, "서비스바꾼이름", null, null, RelationType.FRIEND);

        // then
        Assertions.assertEquals(result, 1);
    }

    @Test @DisplayName("Friend = 단일 친구 삭제")
    public void deleteFriend() throws Exception {
        // given
        Friend friend1 = new Friend("친구1", RelationType.FRIEND, RelationStatus.NORMAL, 50, null, null, null);
        Long savedFriendId1 = friendRepository.save(owner, friend1);

        // when
        int result = friendRepository.deleteById(owner.getId(), friend1.getId());

        // then
        Assertions.assertEquals(result, 1);
    }
}