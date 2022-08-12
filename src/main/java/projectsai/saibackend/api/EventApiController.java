package projectsai.saibackend.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import projectsai.saibackend.domain.Event;
import projectsai.saibackend.domain.Friend;
import projectsai.saibackend.domain.Member;
import projectsai.saibackend.domain.Record;
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
import projectsai.saibackend.service.RecordService;

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
    private final RecordService recordService;

    @PostMapping("/event/add")
    public AddEventResponse addEvent(@RequestBody @Valid AddEventRequest request) {
        try {
            Member owner = em.find(Member.class, request.getOwnerId());
            Event event = new Event(owner, request.getDate(), request.getPurpose(), request.getName(), request.getEvaluation());
            eventService.addEvent(event);
            for (Long friendId : request.getParticipants()) {
                Friend friend = em.find(Friend.class, friendId);
                friend.calcScore(request.getEvaluation());
                friend.calcStatus(friend.getScore());
                Record record = new Record(event, friend);
                em.persist(record);
            }
            return new AddEventResponse(Boolean.TRUE);
        }
        catch(Exception e) {
            log.warn("addEvent Fail: 이벤트 저장 실패 => " + e.getMessage());
            return new AddEventResponse(Boolean.FALSE);
        }
    }

    @PostMapping("/event")
    public List<SearchEventResponse> searchEvents(@RequestBody @Valid SearchEventRequest request) {
        try {
            List<SearchEventResponse> result = new ArrayList<>();
            Member owner = em.find(Member.class, request.getOwnerId());
            List<Event> allEvents = eventService.findAll(owner);

            for (Event event : allEvents) {
                List<Record> recordList = recordService.findAll(event);
                List<Friend> participants = recordList.stream().map(o -> o.getFriend()).collect(Collectors.toList());
                result.add(new SearchEventResponse(event, participants));
            }
            return result;
        }
        catch(Exception e) {
            log.warn("searchEvents Fail: 검색 실패 => " + e.getMessage());
            return null;
        }
    }

    @PutMapping("/event")
    public UpdateEventResponse updateEvent(@RequestBody @Valid UpdateEventRequest request) {

        Event event = eventService.findById(request.getEventId());

        EventEvaluation curnEvaluation = request.getEvaluation();
        List<Friend> curnParticipants = request.getParticipants();

        EventEvaluation prevEvaluation = event.getEvaluation();
        List<Friend> prevParticipants = recordService.findAll(event).stream()
                .map(o -> o.getFriend()).collect(Collectors.toList());

        if(curnParticipants.containsAll(prevParticipants) && !curnEvaluation.equals(prevEvaluation)) {
            friendService.restoreMultipleScore(prevParticipants, prevEvaluation);
            friendService.renewMultipleScore(curnParticipants, curnEvaluation);
        }
        else {
            friendService.restoreMultipleScore(prevParticipants, prevEvaluation);
            recordService.deleteAllRecords(event);
            friendService.renewMultipleScore(curnParticipants, curnEvaluation);
            recordService.addMultipleRecords(event, curnParticipants);
        }

        boolean result = eventService.updateEvent(event.getId(), request.getName(), request.getDate(),
                request.getPurpose(), request.getEvaluation());

        if(result) {
            return new UpdateEventResponse(Boolean.TRUE);
        }
        return new UpdateEventResponse(Boolean.FALSE);
    }

    @DeleteMapping("/event")
    public DeleteEventResponse deleteEvent(@RequestBody @Valid DeleteEventRequest request) {

        Event event = em.find(Event.class, request.getEventId());

        recordService.deleteAllRecords(event);
        boolean result = eventService.deleteEvent(event);

        if(result) {
            return new DeleteEventResponse(Boolean.TRUE);
        }
        return new DeleteEventResponse(Boolean.FALSE);
    }
}