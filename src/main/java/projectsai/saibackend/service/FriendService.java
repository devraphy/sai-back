package projectsai.saibackend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import projectsai.saibackend.domain.Friend;
import projectsai.saibackend.domain.Member;
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
            friendRepository.save(friend);
            log.info("addFriend() Success: 친구 저장 성공");
            return true;
        }
        catch (Exception e) {
            log.error("addFriend() Fail: 친구 저장 실패 => " + e.getMessage());
            return false;
        }
    }

    // 친구 전체 검색
    public List<Friend> findAll(Member owner) {
        try {
            List<Friend> result = friendRepository.findAll(owner);
            log.info("findAll() Success: 모든 친구 검색 성공");
            return result;
        }
        catch (EmptyResultDataAccessException e) {
            log.warn("findAll() Fail: 모든 친구 검색 실패 => " + e.getMessage());
            return null;
        }
    }

    // 친구 ID 검색
    public Friend findById(Long id) {
        try {
            Friend friend = friendRepository.findById(id);
                log.info("findById() Success: 단일 친구 검색 성공");
                return friend;
        }
        catch(EmptyResultDataAccessException e) {
            log.warn("findById() Fail: 단일 친구 검색 실패 => " + e.getMessage());
            return null;
        }
    }

    // 친구 이름 검색
    public List<Friend> findByName(Member owner, String name) {
        try {
            List<Friend> friendList = friendRepository.findByName(owner, name);
            log.info("findByName() Success: 이름으로 친구 검색 성공");
            return friendList;
        }
        catch (EmptyResultDataAccessException e) {
            log.warn("findByName() Fail: 이름으로 친구 검색 실패 => " + e.getMessage());
            return null;
        }
    }

    // 친구 종류 검색
    public List<Friend> findByType(Member owner, RelationType type) {
        try {
            List<Friend> friendList = friendRepository.findByType(owner, type);
            log.info("findByType() Success: 타입으로 친구 검색 성공");
            return friendList;
        }
        catch(EmptyResultDataAccessException e) {
            log.warn("findByType() Fail: 타입으로 친구 검색 실패 => " + e.getMessage());
            return null;
        }
    }

    // 친구 상태 검색
    public List<Friend> findByStatus(Member owner, RelationStatus status) {
        try {
            List<Friend> friendList = friendRepository.findByStatus(owner, status);
            log.info("findByStatus() Success: 상태로로 친구 검색 성공");
            return friendList;
        }
        catch(EmptyResultDataAccessException e) {
            log.warn("findByStatus() Fail: 상태로 친구 검색 실패 => " + e.getMessage());
            return null;
        }
    }

    public List<Friend> findFriends(Member owner, List<Long> friendIds) {
        try {
            List<Friend> friendList = friendRepository.findFriends(owner, friendIds);
            log.info("findFriends() Success: 다수의 친구 아이디로 검색 성공");
            return friendList;
        }
        catch (Exception e) {
            log.warn("findFriends() Fail: 다수의 친구 아이디로 검색 실패 => " + e.getMessage());
            return null;
        }
    }

    // 친구 정보 수정
    @Transactional
    public boolean updateFriend(Long friendId, String name, RelationType type, RelationStatus status,
                             String memo, LocalDate birthDate) {
        try {
            Friend findFriend = friendRepository.findById(friendId);
            findFriend.updateInfo(name, type, status, memo, birthDate);
            em.flush();
            em.clear();
            log.info("updateFriend() Success: 정보 수정 완료");
            return true;
        }
        catch(EmptyResultDataAccessException e) {
            log.info("updateFriend() Fail: 존재하지 않는 ID => " + e.getMessage());
            return false;
        }
    }

    // 친구 삭제
    @Transactional
    public boolean deleteFriendById(Friend friend) {
        try {
            friendRepository.deleteFriendById(friend);
            log.warn("deleteFriend() Success: 친구 삭제 완료");
            return true;
        }
        catch(Exception e) {
            log.warn("deleteFriend() Fail: 친구 삭제 실패 => " + e.getMessage());
            return false;
        }
    }
}
