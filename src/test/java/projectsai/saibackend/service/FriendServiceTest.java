package projectsai.saibackend.service;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import projectsai.saibackend.domain.Friend;
import projectsai.saibackend.domain.Member;
import projectsai.saibackend.domain.enums.EventEvaluation;
import projectsai.saibackend.domain.enums.RelationStatus;
import projectsai.saibackend.domain.enums.RelationType;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
@Transactional
class FriendServiceTest {

    @PersistenceContext
    EntityManager em;
    @Autowired
    FriendService friendService;

    private Member owner;
    private Friend friend1, friend2, friend3, business1, business2, business3;
    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    private void createMember() {
        owner = new Member("라파파", "rapapa@gmail.com", passwordEncoder.encode("abcde"), Boolean.TRUE, "ROLE_USER");
        em.persist(owner);
    }

    @BeforeEach
    private void createFriends() {
        friend1 = new Friend(owner, "친구1", RelationType.FRIEND, RelationStatus.NORMAL, 50, null);
        friend2 = new Friend(owner, "친구2", RelationType.FRIEND, RelationStatus.NORMAL, 50, null);
        friend3 = new Friend(owner, "친구3", RelationType.FRIEND, RelationStatus.NORMAL, 50, null);
        business1 = new Friend(owner, "동료1", RelationType.BUSINESS, RelationStatus.NORMAL, 50, null);
        business2 = new Friend(owner, "동료2", RelationType.BUSINESS, RelationStatus.NORMAL, 50, null);
        business3 = new Friend(owner, "동료3", RelationType.BUSINESS, RelationStatus.NORMAL, 50, null);
        em.persist(friend1);
        em.persist(friend2);
        em.persist(friend3);
        em.persist(business1);
        em.persist(business2);
        em.persist(business3);
    }

    @Test
    @DisplayName("Friend - 친구 등록")
    void save() throws Exception {
        // given
        Friend testFriend = new Friend(owner, "테스트", RelationType.BUSINESS, RelationStatus.NORMAL, 50, null);

        // when
        boolean result = friendService.save(testFriend);

        // then
        Assertions.assertEquals(Boolean.TRUE, result);
    }

    @Test
    @DisplayName("Friend - 모든 친구 검색")
    void findAll() throws Exception {
        // given
        List<Friend> friendList = Arrays.asList(friend1, friend2, friend3, business1, business2, business3);

        // when
        List<Friend> allFriends = friendService.findAll(owner);

        // then
        Assertions.assertEquals(friendList, allFriends);
    }

    @Test
    @DisplayName("Friend - ID로 검색")
    void findById() throws Exception {
        // given

        // when
        Friend findFriend = friendService.findById(friend1.getFriendId());

        // then
        Assertions.assertEquals(friend1, findFriend);
    }

    @Test
    @DisplayName("Friend - 이름으로 검색")
    void findByName() throws Exception {
        // given

        // when
        List<Friend> findFriendList = friendService.findByName(owner, "친구1");

        // then
        for (Friend friend : findFriendList) {
            Assertions.assertEquals(friend1.getName(), friend.getName());
        }
    }

    @Test
    @DisplayName("Friend - 관계 종류로 검색")
    void findByType() throws Exception {
        // given

        // when
        List<Friend> findFriendList = friendService.findByType(owner, RelationType.FRIEND);

        // then
        for (Friend friend : findFriendList) {
            Assertions.assertEquals(friend1.getType(), friend.getType());
        }
    }

    @Test
    @DisplayName("Friend - 관계 상태로 검색")
    void findByStatus() throws Exception {
        // given

        // when
        List<Friend> findFriendList = friendService.findByStatus(owner, RelationStatus.NORMAL);

        // then
        for (Friend friend : findFriendList) {
            Assertions.assertEquals(friend1.getStatus(), friend.getStatus());
        }
    }

    @Test
    @DisplayName("Friend - 다중 ID로 검색")
    public void findFriends() throws Exception {
        //given
        List<Long> friendIds = Arrays.asList(friend1.getFriendId(), friend2.getFriendId(), friend3.getFriendId());

        //when
        List<Friend> findFriends = friendService.findFriends(friendIds);

        //then
        List<Long> ids = findFriends.stream().map(Friend::getFriendId).collect(Collectors.toList());
        Assertions.assertEquals(friendIds, ids);
    }

    @Test
    @DisplayName("Friend - 친구 정보 수정")
    void updateFriend() throws Exception {
        // given
        Friend testFriend = new Friend(owner, "테스트", RelationType.BUSINESS, RelationStatus.NORMAL, 50, null);
        friendService.save(testFriend);

        // when
        friendService.updateFriendInfo(testFriend.getFriendId(), "바꾼이름", RelationType.FRIEND, RelationStatus.POSITIVE, 70, null);

        // then
        Assertions.assertEquals("바꾼이름", testFriend.getName());

    }

    @Test
    @DisplayName("Friend - 다중 친구의 점수 및 상태 갱신")
    void renewMultipleScore() throws Exception {
        // given
        List<Friend> friendList = Arrays.asList(friend1, friend2);
        int score1 = friend1.getScore();
        int score2 = friend2.getScore();

        // when
        friendService.renewMultipleScore(friendList, EventEvaluation.BAD);

        // then
        Assertions.assertEquals(RelationStatus.NEGATIVE, friend1.getStatus());
        Assertions.assertEquals(RelationStatus.NEGATIVE, friend2.getStatus());
    }

    @Test
    @DisplayName("Friend - 다중 친구의 점수 복구")
    void restoreMultipleScore() throws Exception {
        // given
        List<Friend> friendList = Arrays.asList(friend1, friend2);
        int score1 = friend1.getScore();
        int score2 = friend2.getScore();

        // when
        friendService.restoreMultipleScore(friendList, EventEvaluation.BAD);

        // then
        Assertions.assertEquals(60, friend1.getScore());
        Assertions.assertEquals(60, friend2.getScore());
    }

    @Test
    @DisplayName("Friend - 단일 친구의 점수 및 상태 갱신")
    void updateScoreStatus() throws Exception {
        // given

        // when
        friendService.updateScoreStatus(friend1, EventEvaluation.BAD);

        // then
        Assertions.assertEquals(40, friend1.getScore());
        Assertions.assertEquals(RelationStatus.NEGATIVE, friend1.getStatus());
    }


    @Test
    @DisplayName("Friend - 단일 친구 삭제")
    public void deleteFriend() throws Exception {
        // given
        Friend testFriend = new Friend(owner, "테스트", RelationType.BUSINESS, RelationStatus.NORMAL, 50, null);
        friendService.save(testFriend);

        // when
        boolean result = friendService.deleteFriend(testFriend);

        // then
        Assertions.assertTrue(result);
    }
}