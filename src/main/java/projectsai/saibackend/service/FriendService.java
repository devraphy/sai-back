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
            log.info("add Friend Success");
            return true;
        }
        catch (Exception e) {
            log.error("add Friend Fail => " + e.getMessage());
            return false;
        }
    }

    // 친구 전체 검색
    public List<Friend> findAll(Member owner) {
        return friendRepository.findAll(owner);
    }

    // 친구 ID 검색
    public Friend findById(Long id) {
        return friendRepository.findById(id);
    }

    // 친구 이름 검색
    public List<Friend> findByName(Member owner, String name) {
        return friendRepository.findByName(owner, name);
    }

    // 친구 종류 검색
    public List<Friend> findByType(Member owner, RelationType type) {
        return friendRepository.findByType(owner, type);
    }

    // 친구 상태 검색
    public List<Friend> findByStatus(Member owner, RelationStatus status) {
        return friendRepository.findByStatus(owner, status);
    }

    public List<Friend> findFriends(Member owner, List<Long> friendIds) {
        return friendRepository.findFriends(owner, friendIds);
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
        } catch(EmptyResultDataAccessException e) {
            log.info("updateFriend Fail: 존재하지 않는 ID");
            return false;
        }
        log.info("updateFriend Success: 정보 수정 완료");
        return true;
    }

    // 친구 삭제
    @Transactional
    public boolean deleteFriend(Long friendId) {
        try {
            Friend findFriend = friendRepository.findById(friendId);
            em.remove(findFriend);
            em.flush();
            em.clear();
        } catch(EmptyResultDataAccessException e) {
            log.warn("deleteFriend Fail: 존재하지 않는 ID");
            return false;
        }
        log.warn("deleteFriend Success: 삭제 완료");
        return true;
    }
}
