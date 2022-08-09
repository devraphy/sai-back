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
    public Long addFriend(Member member, Friend friend) {
        return friendRepository.save(friend);
    }

    // 친구 전체 검색
    public List<Friend> findAll(Long ownerId) {
        return friendRepository.findAll(ownerId);
    }

    // 친구 ID 검색
    public Friend findById(Long id) {
        return friendRepository.findById(id);
    }

    // 친구 이름 검색
    public List<Friend> findByName(Long ownerId, String name) {
        return friendRepository.findByName(ownerId, name);
    }

    // 친구 종류 검색
    public List<Friend> findByType(Long ownerId, RelationType type) {
        return friendRepository.findByType(ownerId, type);
    }

    // 친구 상태 검색
    public List<Friend> findByStatus(Long ownerId, RelationStatus status) {
        return friendRepository.findByStatus(ownerId, status);
    }

    public List<Friend> findFriends(Long ownerId, List<Long> friendIds) {
        return friendRepository.findFriends(ownerId, friendIds);
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
            log.info("updateFriend: 존재하지 않는 ID");
            return false;
        }
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
            log.warn("deleteFriend: 존재하지 않는 ID");
            return false;
        }
        return true;
    }
}
