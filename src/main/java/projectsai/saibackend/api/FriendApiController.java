package projectsai.saibackend.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import projectsai.saibackend.domain.Friend;
import projectsai.saibackend.domain.Member;
import projectsai.saibackend.domain.Record;
import projectsai.saibackend.domain.enums.RelationStatus;
import projectsai.saibackend.dto.friend.requestDto.AddFriendRequest;
import projectsai.saibackend.dto.friend.requestDto.DeleteFriendRequest;
import projectsai.saibackend.dto.friend.requestDto.SearchFriendRequest;
import projectsai.saibackend.dto.friend.requestDto.UpdateFriendRequest;
import projectsai.saibackend.dto.friend.responseDto.AddFriendResponse;
import projectsai.saibackend.dto.friend.responseDto.DeleteFriendResponse;
import projectsai.saibackend.dto.friend.responseDto.SearchFriendResponse;
import projectsai.saibackend.dto.friend.responseDto.UpdateFriendResponse;
import projectsai.saibackend.service.FriendService;
import projectsai.saibackend.service.RecordService;

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
    private final RecordService recordService;

    @PostMapping("/friend/add") // 친구 추가
    public AddFriendResponse addFriend(@RequestBody @Valid AddFriendRequest request) {

        int score = stringToScore(request.getStatus());

        Member owner = em.find(Member.class, request.getOwnerId());
        Friend friend = new Friend(owner, request.getName(), request.getType(),
                request.getStatus(), score, request.getMemo(), request.getBirthDate());

        if(friendService.addFriend(friend)) {
            return new AddFriendResponse(Boolean.TRUE);
        }
        return new AddFriendResponse(Boolean.FALSE);
    }

    @PostMapping("/friend") // 모든 친구 검색
    public List<SearchFriendResponse> findAll(@RequestBody @Valid SearchFriendRequest request) {
        Member owner = em.find(Member.class, request.getOwnerId());
        List<Friend> allFriends = friendService.findAll(owner);

        List<SearchFriendResponse> result = allFriends.stream()
                .map(o -> new SearchFriendResponse(o)).collect(toList());

        return result;
    }

    @PutMapping("/friend") // 친구 수정
    public UpdateFriendResponse updateFriend(@RequestBody @Valid UpdateFriendRequest request) {
        try {
            friendService.updateFriend(request.getFriendId(), request.getName(), request.getType(),
                    request.getStatus(), request.getMemo(), request.getBirthDate());
        } catch(Exception e) {
            return new UpdateFriendResponse(Boolean.FALSE);
        }
        return new UpdateFriendResponse(Boolean.TRUE);
    }

    @DeleteMapping("/friend")
    public DeleteFriendResponse deleteFriend(@RequestBody @Valid DeleteFriendRequest request) {
        Friend friend = friendService.findById(request.getFriendId());
        List<Record> recordList = recordService.findByParticipant(friend);

        for (Record record : recordList) {
            recordService.deleteRecord(record);
        }

        boolean result = friendService.deleteFriend(friend);

        if(result) {
            return new DeleteFriendResponse(Boolean.TRUE);
        }
        return new DeleteFriendResponse(Boolean.FALSE);
    }

    // Business Methods
    private int stringToScore(RelationStatus status) {
        if(status.equals(RelationStatus.BAD)) return 10;
        else if(status.equals(RelationStatus.NEGATIVE)) return 30;
        else if(status.equals(RelationStatus.NORMAL)) return 50;
        else if(status.equals(RelationStatus.POSITIVE)) return 70;
        else return 90;
    }
}
