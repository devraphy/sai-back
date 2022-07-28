package projectsai.saibackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import projectsai.saibackend.domain.Friend;
import projectsai.saibackend.domain.Member;
import projectsai.saibackend.domain.enums.RelationStatus;
import projectsai.saibackend.domain.enums.RelationType;
import projectsai.saibackend.repository.FriendRepository;

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
    public List<Friend> findAll(Member member) {
        return friendRepository.findAll(member);
    }

    // 친구 이름 검색
    public List<Friend> findByName(Member member, String name) {
        return friendRepository.findByName(member, name);
    }

    // 친구 종류 검색
    public List<Friend> findByType(Member member, RelationType type) {
        return friendRepository.findByType(member, type);
    }

    // 친구 상태 검색
    public List<Friend> findByStatus(Member member, RelationStatus status) {
        return friendRepository.findByStatus(member, status);
    }
}
