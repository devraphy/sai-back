package projectsai.saibackend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import projectsai.saibackend.domain.Friend;
import projectsai.saibackend.domain.Member;
import projectsai.saibackend.domain.enums.EventEvaluation;
import projectsai.saibackend.domain.enums.RelationStatus;
import projectsai.saibackend.domain.enums.RelationType;
import projectsai.saibackend.repository.FriendRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.List;

@Service @Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FriendService {

    @PersistenceContext EntityManager em;
    private final FriendRepository friendRepository;

    // 친구 저장
    @Transactional
    public boolean addFriend(Friend friend) {
        try {
            friendRepository.addFriend(friend);
            log.info("Friend | addFriend() Success: 저장 성공");
            return true;
        }
        catch (Exception e) {
            log.error("Friend | addFriend() Fail: 에러 발생 => " + e.getMessage());
            return false;
        }
    }

    // 친구 전체 검색
    public List<Friend> findAll(Member owner) {
        try {
            List<Friend> result = friendRepository.findAll(owner);
            log.info("Friend | findAll() Success: 검색 성공");
            return result;
        }
        catch (EmptyResultDataAccessException e) {
            log.warn("Friend | findAll() Fail: 검색 결과 없음 => " + e.getMessage());
            return null;
        }
    }

    // 친구 ID 검색
    public Friend findById(Long id) {
        try {
            Friend friend = friendRepository.findById(id);
                log.info("Friend | findById() Success: 검색 성공");
                return friend;
        }
        catch(EmptyResultDataAccessException e) {
            log.warn("Friend | findById() Fail: 검색 결과 없음 => " + e.getMessage());
            return null;
        }
    }

    // 친구 이름 검색
    public List<Friend> findByName(Member owner, String name) {
        try {
            List<Friend> friendList = friendRepository.findByName(owner, name);
            log.info("Friend | findByName() Success: 검색 성공");
            return friendList;
        }
        catch (EmptyResultDataAccessException e) {
            log.warn("Friend | findByName() Fail: 검색 결과 없음 => " + e.getMessage());
            return null;
        }
    }

    // 친구 종류 검색
    public List<Friend> findByType(Member owner, RelationType type) {
        try {
            List<Friend> friendList = friendRepository.findByType(owner, type);
            log.info("Friend | findByType() Success: 검색 성공");
            return friendList;
        }
        catch(EmptyResultDataAccessException e) {
            log.warn("Friend | findByType() Fail: 검색 결과 없음 => " + e.getMessage());
            return null;
        }
    }

    // 친구 상태 검색
    public List<Friend> findByStatus(Member owner, RelationStatus status) {
        try {
            List<Friend> friendList = friendRepository.findByStatus(owner, status);
            log.info("Friend | findByStatus() Success: 검색 성공");
            return friendList;
        }
        catch(EmptyResultDataAccessException e) {
            log.warn("Friend | findByStatus() Fail: 검색 결과 없음 => " + e.getMessage());
            return null;
        }
    }

    // 다수의 Id를 이용한 친구 검색
    public List<Friend> findFriends(Member owner, List<Long> friendIds) {
        try {
            List<Friend> friendList = friendRepository.findByIds(owner, friendIds);
            log.info("Friend | findFriends() Success: 검색 성공");
            return friendList;
        }
        catch (Exception e) {
            log.warn("Friend | findFriends() Fail: 검색 결과 없음 => " + e.getMessage());
            return null;
        }
    }

    // 단일 친구 정보 수정
    @Transactional
    public boolean updateFriend(Long friendId, String name, RelationType type, RelationStatus status,
                             String memo, LocalDate birthDate) {
        try {
            Friend findFriend = friendRepository.findById(friendId);
            findFriend.updateInfo(name, type, status, memo, birthDate);
            findFriend.calcStatus(findFriend.getScore());

            em.flush();
            em.clear();

            log.info("Friend | updateFriend() Success: 수정 완료");
            return true;
        }
        catch(EmptyResultDataAccessException e) {
            log.info("Friend | updateFriend() Fail: 존재하지 않는 ID => " + e.getMessage());
            return false;
        }
    }

    // 다수의 친구 점수를 복구(수정)
    public boolean restoreMultipleScore(List<Friend> prevParticipants, EventEvaluation prevEvaluation) {
        try {
            for (Friend friend : prevParticipants) {
                friend.restoreScore(prevEvaluation);
            }
            em.flush();
            em.clear();

            log.info("Friend | restoreScore() Success: 수정 성공");
            return true;
        }
        catch(Exception e) {
            log.warn("Friend | restoreScore() Fail: 에러 발생 => " + e.getMessage());
            return false;
        }
    }

    // 다수의 친구 점수 및 상태를 갱신(수정)
    public boolean renewMultipleScore(List<Friend> curnParticipants, EventEvaluation curnEvaluation) {
        try {
            for (Friend friend : curnParticipants) {
                friend.calcScore(curnEvaluation);
                friend.calcStatus(friend.getScore());
            }
            em.flush();
            em.clear();

            log.info("Friend | renewMultipleScore() Success: 수정 성공");
            return true;
        }
        catch (Exception e) {
            log.warn("Friend | renewMultipleScore() Success: 에러 발생 => " + e.getMessage());
            return false;
        }
    }

    public boolean updateScoreStatus(Friend friend, EventEvaluation evaluation) {
        try {
            friend.calcScore(evaluation);
            friend.calcStatus(friend.getScore());

            em.flush();
            em.clear();

            log.info("Friend | updateScoreStatus() Success: 수정 성공");
            return true;
        }
        catch (Exception e) {
            log.warn("Friend | updateScoreStatus() Success: 에러 발생 => " + e.getMessage());
            return false;
        }
    }

    // 친구 삭제
    @Transactional
    public boolean deleteFriend(Friend friend) {
        try {
            friendRepository.deleteFriend(friend);

            em.flush();
            em.clear();

            log.warn("Friend | deleteFriend() Success: 삭제 성공");
            return true;
        }
        catch(Exception e) {
            log.warn("Friend | deleteFriend() Fail: 에러 발생 => " + e.getMessage());
            return false;
        }
    }
}
