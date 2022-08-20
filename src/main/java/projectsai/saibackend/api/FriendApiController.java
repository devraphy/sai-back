package projectsai.saibackend.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import projectsai.saibackend.domain.Friend;
import projectsai.saibackend.domain.User;
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

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.Valid;
import java.util.List;

import static java.util.stream.Collectors.*;

@RestController @Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/friend")
public class FriendApiController {

    @PersistenceContext EntityManager em;
    private final FriendService friendService;

    @PostMapping("/add") // 친구 추가
    public AddFriendResponse addFriend(@RequestBody @Valid AddFriendRequest request) {

        int score = setInitialScore(request.getStatus());
        User owner = em.find(User.class, request.getOwnerId());
        Friend friend = new Friend(owner, request.getName(), request.getType(),
                request.getStatus(), score, request.getMemo(), request.getBirthDate());

        if(friendService.addFriend(friend)) {
            log.info("Friend API | addFriend() Success: 친구 저장 성공");
            return new AddFriendResponse(Boolean.TRUE);
        }
        log.warn("Friend API | addFriend() Fail: 친구 저장 실패");
        return new AddFriendResponse(Boolean.FALSE);
    }

    @PostMapping("/search") // 모든 친구 검색
    public List<SearchFriendResponse> findAll(@RequestBody @Valid SearchFriendRequest request) {

        try {
            User owner = em.find(User.class, request.getOwnerId());
            List<Friend> allFriends = friendService.findAll(owner);

            List<SearchFriendResponse> result = allFriends.stream()
                    .map(o -> new SearchFriendResponse(o)).collect(toList());

            log.info("Friend API | findAll() Success: 검색 성공");
            return result;
        }
        catch(Exception e) {
            log.warn("Friend API | findAll() Fail: 검색 실피");
            return null;
        }
    }

    @PutMapping("/update") // 친구 수정
    public UpdateFriendResponse updateFriend(@RequestBody @Valid UpdateFriendRequest request) {

        try {
            Integer score = setInitialScore(request.getStatus());
            friendService.updateFriend(request.getFriendId(), request.getName(), request.getType(),
                    request.getStatus(), score, request.getMemo(), request.getBirthDate());
        }
        catch(Exception e) {
            log.info("Friend API | updateFriend() Fail: 친구 수정 실패");
            return new UpdateFriendResponse(Boolean.FALSE);
        }
        log.info("Friend API | updateFriend() Success: 친구 수정 성공");
        return new UpdateFriendResponse(Boolean.TRUE);
    }

    @DeleteMapping("/delete")
    public DeleteFriendResponse deleteFriend(@RequestBody @Valid DeleteFriendRequest request) {

        Friend friend = friendService.findById(request.getFriendId());
        boolean result = friendService.deleteFriend(friend);

        if(result) {
            log.info("Friend API | deleteFriend() Success: 친구 삭제 성공");
            return new DeleteFriendResponse(Boolean.TRUE);
        }
        log.warn("Friend API | deleteFriend() Fail: 친구 삭제 실패");
        return new DeleteFriendResponse(Boolean.FALSE);
    }

    // Business Methods
    private int setInitialScore(RelationStatus status) {

        if(status.equals(RelationStatus.BAD)) return 10;
        else if(status.equals(RelationStatus.NEGATIVE)) return 30;
        else if(status.equals(RelationStatus.NORMAL)) return 50;
        else if(status.equals(RelationStatus.POSITIVE)) return 70;
        else return 90;
    }
}
