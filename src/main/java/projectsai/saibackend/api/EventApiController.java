package projectsai.saibackend.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import projectsai.saibackend.domain.Event;
import projectsai.saibackend.domain.Friend;
import projectsai.saibackend.domain.User;
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
@RequestMapping("/api/event")
public class EventApiController {

    @PersistenceContext EntityManager em;
    private final EventService eventService;
    private final FriendService friendService;
    private final RecordService recordService;

    @PostMapping("/add") // 이벤트 - 저장
    public AddEventResponse addEvent(@RequestBody @Valid AddEventRequest request) {
        try {
            User owner = em.find(User.class, request.getOwnerId());
            Event event = new Event(owner, request.getDate(), request.getPurpose(),
                    request.getName(), request.getEvaluation());

            eventService.addEvent(event);

            for (Long id : request.getParticipants()) {
                Friend friend = friendService.findById(id);
                recordService.addRecord(new Record(event, friend));
                friendService.updateScoreStatus(friend, request.getEvaluation());
            }
            log.info("Event API | addEvent() Success: 이벤트 저장 성공");
            return new AddEventResponse(Boolean.TRUE);
        }
        catch(Exception e) {
            log.warn("Event API | addEvent() Fail: 이벤트 저장 실패 => " + e.getMessage());
            return new AddEventResponse(Boolean.FALSE);
        }
    }

    @PostMapping("/search") // 이벤트 - 소유한 전체 이벤트 검색
    public List<SearchEventResponse> searchEvents(@RequestBody @Valid SearchEventRequest request) {
        try {
            List<SearchEventResponse> result = new ArrayList<>();
            User owner = em.find(User.class, request.getOwnerId());
            List<Event> allEvents = eventService.findAll(owner);

            for (Event event : allEvents) {
                List<Record> recordList = recordService.findAll(event);
                List<Long> friendIds = recordList.stream().map(o -> o.getFriend().getFriendId()).collect(Collectors.toList());
                List<Friend> friendList = friendService.findFriends(friendIds);
                result.add(new SearchEventResponse(event, friendList));
            }
            log.info("Event API | searchEvents() Success: 이벤트 검색 성공");
            return result;
        }
        catch(Exception e) {
            log.warn("Event API | searchEvents() Fail: 검색 실패 => " + e.getMessage());
            return null;
        }
    }

    @PutMapping("/update") // 이벤트 - 특정 이벤트 수정
    public UpdateEventResponse updateEvent(@RequestBody @Valid UpdateEventRequest request) {

        Event event = eventService.findById(request.getEventId());
        EventEvaluation curnEvaluation = request.getEvaluation();
        EventEvaluation prevEvaluation = event.getEvaluation();
        List<Long> participants = request.getParticipants();
        List<Record> recordList = recordService.findAll(event);
        List<Friend> curnParticipants = friendService.findFriends(participants);
        List<Friend> prevParticipants = recordList.stream()
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

        boolean result = eventService.updateEvent(event.getEventId(), request.getName(), request.getDate(),
                request.getPurpose(), request.getEvaluation());

        if(result) {
            log.info("Event API | updateEvent() Success: 이벤트 수정 성공");
            return new UpdateEventResponse(Boolean.TRUE);
        }
        log.warn("Event API | updateEvent() Fail: 수정 실패");
        return new UpdateEventResponse(Boolean.FALSE);
    }

    @DeleteMapping("/delete") // 이벤트 - 삭제
    public DeleteEventResponse deleteEvent(@RequestBody @Valid DeleteEventRequest request) {

        Event event = em.find(Event.class, request.getEventId());
        boolean result = eventService.deleteEvent(event);
        if(result) {
            log.info("Event API | deleteEvent() Success: 이벤트 삭제 성공");
            return new DeleteEventResponse(Boolean.TRUE);
        }
        log.warn("Event API | deleteEvent() Fail: 삭제 실패");
        return new DeleteEventResponse(Boolean.FALSE);
    }
}