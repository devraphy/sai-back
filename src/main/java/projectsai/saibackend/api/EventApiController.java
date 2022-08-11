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
import projectsai.saibackend.service.RecordService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController @Slf4j
@RequiredArgsConstructor
public class EventApiController {

    @PersistenceContext EntityManager em;
    private final EventService eventService;
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
                List participants = em.createQuery("select r.friend " +
                                "from Record r " +
                                "where r.event = :event")
                        .setParameter("event", event)
                        .getResultList();
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
        try {
            Event event = eventService.findById(request.getEventId());
            EventEvaluation prevEvaluation = event.getEvaluation();
            EventEvaluation newEvaluation = request.getEvaluation();
            List<Record> prevParticipants = recordService.findAll(event);
            List<Friend> newParticipants = request.getParticipants();

            if(!newParticipants.containsAll(prevParticipants) && !newEvaluation.equals(prevEvaluation)){
                em.createQuery("delete from Record r where r.event = :event")
                        .setParameter("event", event)
                        .executeUpdate();

                for (Friend participant : request.getParticipants()) {
                    participant.calcScore(request.getEvaluation());
                    participant.calcStatus(participant.getScore());
                    Record record = new Record(event, participant);
                    em.persist(record);
                }
            }
            else if(!newParticipants.containsAll(prevParticipants) && newEvaluation.equals(prevEvaluation)){
                em.createQuery("delete from Record r where r.event = :event")
                        .setParameter("event", event)
                        .executeUpdate();

                for (Friend participant : request.getParticipants()) {
                    Record record = new Record(event, participant);
                    em.persist(record);
                }
            }
            else if(!newEvaluation.equals(prevEvaluation)) {
                for (Friend participant : request.getParticipants()) {
                    participant.calcScore(request.getEvaluation());
                    participant.calcStatus(participant.getScore());
                }
            }

            event.updateInfo(request.getName(), request.getDate(), request.getPurpose(), request.getEvaluation());
            em.flush();
            em.clear();
            return new UpdateEventResponse(Boolean.TRUE);
        }
        catch(Exception e) {
            log.warn("updateEvent Fail: 이벤트 수정 실패 => " + e.getMessage());
            return new UpdateEventResponse(Boolean.FALSE);
        }
    }

    @DeleteMapping("/event")
    public DeleteEventResponse deleteEvent(@RequestBody @Valid DeleteEventRequest request) {
        try {
            Event event = em.find(Event.class, request.getEventId());
            recordService.deleteAllRecord(event);
            eventService.deleteEvent(event);
            log.info("deleteEvent Success: 이벤트 삭제 성공");
            return new DeleteEventResponse(Boolean.TRUE);
        }
        catch(Exception e) {
            log.warn("deleteEvent Fail: 이벤트 삭제 실패 => " + e.getMessage());
            return new DeleteEventResponse(Boolean.FALSE);
        }
    }
}