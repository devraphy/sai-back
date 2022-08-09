package projectsai.saibackend.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.web.bind.annotation.*;
import projectsai.saibackend.domain.Event;
import projectsai.saibackend.domain.Friend;
import projectsai.saibackend.domain.Member;
import projectsai.saibackend.domain.enums.EventEvaluation;
import projectsai.saibackend.dto.event.requestDto.AddEventRequest;
import projectsai.saibackend.dto.event.requestDto.DeleteEventRequest;
import projectsai.saibackend.dto.event.requestDto.SearchEventRequest;
import projectsai.saibackend.dto.event.requestDto.UpdateEventRequest;
import projectsai.saibackend.dto.event.responseDto.AddEventResponse;
import projectsai.saibackend.dto.event.responseDto.DeleteEventResponse;
import projectsai.saibackend.dto.event.responseDto.SearchEventResponse;
import projectsai.saibackend.dto.event.responseDto.UpdateEventResponse;
import projectsai.saibackend.service.EventService;
import projectsai.saibackend.service.FriendService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController @Slf4j
@RequiredArgsConstructor
public class EventApiController {

    @PersistenceContext EntityManager em;
    private final EventService eventService;
    private final FriendService friendService;

    @PostMapping("/event/add")
    public AddEventResponse addEvent(@RequestBody @Valid AddEventRequest request) {
        Long ownerId = request.getOwnerId();
        Member owner = em.find(Member.class, ownerId);
        EventEvaluation evaluation = request.getEvaluation();

        List<Friend> participants = new ArrayList<>();
        List<Friend> friendList = friendService.findFriends(owner.getId(), request.getParticipants());

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

    @PostMapping("/event")
    public List<SearchEventResponse> searchEvents(@RequestBody @Valid SearchEventRequest request) {
        List<Event> eventList = eventService.findAll(request.getOwnerId());

        List<SearchEventResponse> result = eventList.stream()
                .map(o -> new SearchEventResponse(o)).collect(Collectors.toList());

        return result;
    }

    @PutMapping("/event")
    public UpdateEventResponse updateEvent(@RequestBody @Valid UpdateEventRequest request) {
        try {
            Event findEvent = eventService.findById(request.getEventId());
            findEvent.updateInfo(request.getName(), request.getDate(),
                    request.getEvaluation(), request.getParticipants());
        }
        catch(EmptyResultDataAccessException e) {
            log.warn("updateEvent: 존재하지 않는 이벤트 ID");
            return new UpdateEventResponse(Boolean.FALSE);
        }
        return new UpdateEventResponse(Boolean.TRUE);
    }

    @DeleteMapping("/event")
    public DeleteEventResponse deleteEvent(@RequestBody @Valid DeleteEventRequest request) {
        try {
            eventService.deleteEvent(request.getEventId(), request.getFriendId());

        } catch(EmptyResultDataAccessException e) {
            log.warn("deleteEvent: ownerId와 friendId에 매칭된 이벤트가 없습니다.");
            return new DeleteEventResponse(Boolean.FALSE);

        }
        log.info("deleteEvent: 이벤트 삭제 성공");
        return new DeleteEventResponse(Boolean.TRUE);
    }
}
//SELECT * FROM MEMBER ;
//        SELECT * FROM FRIEND  ;
//        SELECT * FROM EVENT  ;
//        SELECT * FROM EVENT_PARTICIPANTS  ;