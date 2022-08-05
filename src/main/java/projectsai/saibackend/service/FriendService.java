package projectsai.saibackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import projectsai.saibackend.domain.Friend;
import projectsai.saibackend.domain.Member;
import projectsai.saibackend.domain.enums.RelationStatus;
import projectsai.saibackend.domain.enums.RelationType;
import projectsai.saibackend.repository.FriendRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FriendService {

    private final FriendRepository friendRepository;

    // 친구 저장
    @Transactional
    public Long addFriend(Member member, Friend friend) {
        return friendRepository.save(member, friend);
    }

    // 친구 전체 검색
    public List<Friend> findAll(Long ownerId) {
        return friendRepository.findAll(ownerId);
    }

    // 친구 ID 검색
    public Friend findById(Long ownerId, Long id) {
        return friendRepository.findById(ownerId, id);
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
    public int updateFriend(Long ownerId, Long friendId, String name, LocalDate birthDate, String memo, RelationType friendType) {
        return friendRepository.updateById(ownerId, friendId, name, birthDate, memo, friendType);
    }

    // 친구 삭제 ==> 실행 이전에 event 삭제 작업부터 실행되어야 한다.
    @Transactional
    public int deleteFriend(Long ownerId, Long friendId) {
        return friendRepository.deleteById(ownerId, friendId);
    }
}
