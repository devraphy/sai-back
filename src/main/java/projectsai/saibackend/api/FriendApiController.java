package projectsai.saibackend.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import projectsai.saibackend.domain.Friend;
import projectsai.saibackend.domain.Member;
import projectsai.saibackend.domain.enums.RelationStatus;
import projectsai.saibackend.domain.enums.RelationType;
import projectsai.saibackend.dto.friend.requestDto.AddFriendRequest;
import projectsai.saibackend.dto.friend.requestDto.DeleteFriendRequest;
import projectsai.saibackend.dto.friend.requestDto.SearchFriendRequest;
import projectsai.saibackend.dto.friend.requestDto.UpdateFriendRequest;
import projectsai.saibackend.dto.friend.responseDto.AddFriendResponse;
import projectsai.saibackend.dto.friend.responseDto.DeleteFriendResponse;
import projectsai.saibackend.dto.friend.responseDto.SearchFriendResponse;
import projectsai.saibackend.dto.friend.responseDto.UpdateFriendResponse;
import projectsai.saibackend.service.EventService;
import projectsai.saibackend.service.FriendService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.Valid;
import java.util.List;

import static java.util.stream.Collectors.*;

@RestController
@RequiredArgsConstructor
public class FriendApiController {

    @PersistenceContext EntityManager em;
    private final FriendService friendService;
    private final EventService eventService;

    @PostMapping("/friend/add") // 친구 추가
    public AddFriendResponse addFriend(@RequestBody @Valid AddFriendRequest request) {

        int score = stringToScore(request.getRelationStatus());
        RelationType relationType = stringToType(request.getRelationType());
        RelationStatus relationStatus = stringToStatus(request.getRelationStatus());

        Member owner = em.find(Member.class, request.getOwnerId());
        Friend friend = new Friend(request.getName(), relationType,
                relationStatus, score, request.getMemo(), request.getBirthDate());

        try {
            friendService.addFriend(owner, friend);
        }
        catch (Exception e) {
            return new AddFriendResponse(Boolean.FALSE);
        }
        return new AddFriendResponse(Boolean.TRUE);
    }

    @PostMapping("/friend") // 모든 친구 검색
    public List<SearchFriendResponse> findAll(@RequestBody @Valid SearchFriendRequest request) {
        List<Friend> allFriends = friendService.findAll(request.getOwnerId());

        List<SearchFriendResponse> result = allFriends.stream()
                .map(o -> new SearchFriendResponse(o)).collect(toList());

        return result;
    }

    @PutMapping("/friend") // 친구 수정
    public UpdateFriendResponse updateFriend(@RequestBody @Valid UpdateFriendRequest request) {
        RelationType relationType = stringToType(request.getRelationType());

        int result = friendService.updateFriend(request.getOwnerId(), request.getFriendId(), request.getName(),
                request.getBirthDate(), request.getMemo(), relationType);

        if(result == 1) return new UpdateFriendResponse(Boolean.TRUE);
        else return new UpdateFriendResponse(Boolean.FALSE);
    }

    @DeleteMapping("/friend") // 친구 삭제 => Event와 연관된 문제 발생
    public DeleteFriendResponse deleteFriend(@RequestBody @Valid DeleteFriendRequest request) {
        int result = friendService.deleteFriend(request.getOwnerId(), request.getFriendId());

        if(result == 1) return new DeleteFriendResponse(Boolean.TRUE);
        else return new DeleteFriendResponse(Boolean.FALSE);
    }

    // 최초 친구 추가 시점에서 String 타입의 관계 상태를 int score 로 변환
    private int stringToScore(String status) {
        if(status.equals("BAD")) return 10;
        else if(status.equals("NEGATIVE")) return 30;
        else if(status.equals("NORMAL")) return 50;
        else if(status.equals("POSITIVE")) return 70;
        else return 90;
    }

    // 최초 친구 추가 시점에서 String 타입의 관계 상태를 RelationStatus 로 변환
    private RelationStatus stringToStatus(String status) {
        if(status.equals("BAD")) return RelationStatus.BAD;
        else if(status.equals("NEGATIVE")) return RelationStatus.NEGATIVE;
        else if(status.equals("NORMAL")) return RelationStatus.NORMAL;
        else if(status.equals("POSITIVE")) return RelationStatus.POSITIVE;
        else return RelationStatus.STRONG;
    }

    private RelationType stringToType(String type) {
        if(type.equals("FRIEND")) return RelationType.FRIEND;
        else return RelationType.BUSINESS;
    }
}
