package projectsai.saibackend.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import projectsai.saibackend.domain.Event;
import projectsai.saibackend.domain.Friend;
import projectsai.saibackend.domain.Member;
import projectsai.saibackend.domain.enums.EventEvaluation;
import projectsai.saibackend.dto.event.requestDto.AddEventRequest;
import projectsai.saibackend.dto.event.responseDto.AddEventResponse;
import projectsai.saibackend.service.EventService;
import projectsai.saibackend.service.FriendService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class EventApiController {

    @PersistenceContext EntityManager em;
    private final EventService eventService;
    private final FriendService friendService;

    @PostMapping("/event/add")
    public AddEventResponse addEvent(@RequestBody @Valid AddEventRequest request) {
        Member owner = em.find(Member.class, request.getOwnerId());
        List<Friend> participants = new ArrayList<>();

        Long ownerId = request.getOwnerId();
        List<Friend> friendList = em.createQuery("select f from Friend f " +
                        "where f.owner.id = :ownerId " +
                        "and f.id in :friendIds", Friend.class)
                .setParameter("ownerId", ownerId)
                .setParameter("friendIds", request.getParticipants())
                .getResultList();

        EventEvaluation evaluation = request.getEvaluation();

        // 이벤트 등록시, evaluation을 기준으로 관계 점수 계산
        for(Friend friend : friendList) {
            participants.add(friend);
            friend.calcScore(evaluation);
            friend.calcStatus(friend.getScore());
        }

        Event event = new Event(request.getDate(), request.getPurpose(), request.getName(),
                request.getEvaluation(), participants);

        Long savedEventId;

        try {
            owner.addEvent(event);
            savedEventId = event.getId();
        }
        catch(Exception e) {
            return new AddEventResponse(null, Boolean.FALSE);
        }
        return new AddEventResponse(savedEventId, Boolean.TRUE);
    }
}
