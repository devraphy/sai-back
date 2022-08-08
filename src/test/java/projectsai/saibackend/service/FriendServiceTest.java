package projectsai.saibackend.service;

import com.mysema.commons.lang.Assert;
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
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional @Slf4j
class FriendServiceTest {

    @PersistenceContext EntityManager em;
    @Autowired FriendRepository friendRepository;
    @Autowired FriendService friendService;

    private Member owner;
    private Friend friend1, friend2, friend3, business1, business2, business3;

    @BeforeEach
    private void createMember() {
        owner = new Member("라파파", "rapapa@gmail.com", "abcde", LocalDate.now(), Boolean.TRUE);
        em.persist(owner);
    }

    @BeforeEach
    private void createFriends() {
        friend1 = new Friend("친구1", RelationType.FRIEND, RelationStatus.NORMAL, 50, null, null);
        friend2 = new Friend("친구2", RelationType.FRIEND, RelationStatus.NORMAL, 50, null, null);
        friend3 = new Friend("친구3", RelationType.FRIEND, RelationStatus.NORMAL, 50, null, null);
        business1 = new Friend("동료1", RelationType.BUSINESS, RelationStatus.NORMAL, 50, null, null);
        business2 = new Friend("동료2", RelationType.BUSINESS, RelationStatus.NORMAL, 50, null, null);
        business3 = new Friend("동료3", RelationType.BUSINESS, RelationStatus.NORMAL, 50, null, null);
        friendRepository.save(owner, friend1);
        friendRepository.save(owner, friend2);
        friendRepository.save(owner, friend3);
        friendRepository.save(owner, business1);
        friendRepository.save(owner, business2);
        friendRepository.save(owner, business3);
    }

    @Test @DisplayName("Friend - 친구 등록")
    void add() {
        // given
        Friend testFriend = new Friend("테스트", RelationType.BUSINESS, RelationStatus.NORMAL, 50, null, null);

        // when
        Long savedFriendId = friendRepository.save(owner, testFriend);

        // then
        Assertions.assertEquals(testFriend.getId(), savedFriendId);
    }

    @Test @DisplayName("Friend - 모든 친구 검색")
    void findAll() {
        // given
        List<Friend> friendList = new ArrayList<>();
        friendList.add(friend1);
        friendList.add(friend2);
        friendList.add(friend3);
        friendList.add(business1);
        friendList.add(business2);
        friendList.add(business3);

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

        // when
        Friend findFriend = friendRepository.findById(friend1.getId());

        // then
        Assertions.assertEquals(friend1, findFriend);
    }

    @Test @DisplayName("Friend - 이름으로 검색")
    void findByName() {
        // given

        // when
        List<Friend> findFriendList = friendRepository.findByName(owner.getId(), "친구1");

        // then
        for(Friend friend : findFriendList) {
            Assertions.assertEquals(friend1.getName(), friend.getName());
        }
    }

    @Test @DisplayName("Friend - 관계 종류로 검색")
    void findByType() {
        // given

        // when
        List<Friend> findFriendList = friendRepository.findByType(owner.getId(), RelationType.FRIEND);

        // then
        for(Friend friend : findFriendList) {
            Assertions.assertEquals(friend1.getType(), friend.getType());
        }
    }

    @Test @DisplayName("Friend - 관계 상태로 검색")
    void findByStatus() {
        // given

        // when
        List<Friend> findFriendList = friendRepository.findByStatus(owner.getId(), RelationStatus.NORMAL);

        // then
        for(Friend friend : findFriendList) {
            Assertions.assertEquals(friend1.getStatus(), friend.getStatus());
        }
    }

    @Test @DisplayName("Friend - 다중 ID로 검색")
    public void findFriends() throws Exception {
       //given
        List<Long> friendIds = new ArrayList<>();
        friendIds.add(friend1.getId());
        friendIds.add(friend2.getId());
        friendIds.add(friend3.getId());

       //when
        List<Friend> friends = friendRepository.findFriends(owner.getId(), friendIds);

        //then
        List<Long> ids = friends.stream().map(o -> o.getId()).collect(Collectors.toList());
        Assertions.assertEquals(friendIds, ids);
    }

    @Test @DisplayName("Friend - 친구 정보 수정")
    void updateById() {
        // given
        Friend testFriend = new Friend("테스트", RelationType.BUSINESS, RelationStatus.NORMAL, 50, null, null);
        Long savedFriendId1 = friendRepository.save(owner, testFriend);

        // when
        boolean result = friendService.updateFriend(savedFriendId1, "바꾼이름", RelationType.FRIEND, RelationStatus.POSITIVE, null, null);

        // then
        Assertions.assertEquals( "바꾼이름", testFriend.getName());

    }

    @Test @DisplayName("Friend = 단일 친구 삭제")
    public void deleteFriend() throws Exception {
        // given
        Friend testFriend = new Friend("테스트", RelationType.BUSINESS, RelationStatus.NORMAL, 50, null, null);
        Long savedFriendId1 = friendRepository.save(owner, testFriend);

        // when
        boolean result = friendService.deleteFriend(savedFriendId1);

        // then
        Assertions.assertEquals(true, result);
    }
}