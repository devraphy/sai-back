package projectsai.saibackend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import projectsai.saibackend.domain.Event;
import projectsai.saibackend.domain.Friend;
import projectsai.saibackend.domain.User;
import projectsai.saibackend.domain.Record;
import projectsai.saibackend.domain.enums.EventEvaluation;
import projectsai.saibackend.domain.enums.EventPurpose;
import projectsai.saibackend.repository.EventRepository;
import projectsai.saibackend.repository.RecordRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service @Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EventService {

    @PersistenceContext EntityManager em;
    private final EventRepository eventRepository;
    private final RecordRepository recordRepository;

    // 이벤트 저장
    @Transactional
    public Long addEvent(Event event) {
        try {
            Long savedEventId = eventRepository.addEvent(event);
            log.info("Event Service | addEvent() Success: 저장 성공");
            return savedEventId;
        }
        catch (Exception e) {
            log.warn("Event Service | addEvent() Fail: 에러 발생 => " + e.getMessage());
            return null;
        }
    }

    // 이벤트 ID 검색
    public Event findById(Long eventId) {
        try {
            Event event = eventRepository.findById(eventId);
            log.info("Event Service | findById() Success: 검색 성공");
            return event;
        }
        catch (EmptyResultDataAccessException e) {
            log.warn("Event Service | findById() Fail: 검색 결과 없음 => " + e.getMessage());
            return null;
        }
    }

    // 이벤트 전체 검색
    public List<Event> findAll(User owner) {
        try {
            List<Event> eventList = eventRepository.findAll(owner);
            log.info("Event Service | findAll() Success: 검색 성공");
            return eventList;
        }
        catch (EmptyResultDataAccessException e) {
            log.warn("Event Service | findAll() Fail: 검색 결과 없음 => " + e.getMessage());
            return null;
        }
    }

    // 이벤트 이름 검색
    public List<Event> findByName(User owner, String name) {
        try {
            List<Event> eventList = eventRepository.findByEventName(owner, name);
            log.info("Event Service | findByName() Success: 검색 성공");
            return eventList;
        }
        catch (EmptyResultDataAccessException e) {
            log.warn("Event Service | findByName() Fail: 검색 결과 없음 => " + e.getMessage());
            return null;
        }
    }

    // 이벤트 날짜 검색
    public List<Event> findByDate(User owner, LocalDate date) {
        try {
            List<Event> eventList = eventRepository.findByDate(owner, date);
            log.info("Event Service | findByDate() Success: 검색 성공");
            return eventList;
        }
        catch (EmptyResultDataAccessException e) {
            log.warn("Event Service | findByDate() Fail: 검색 결과 없음 => " + e.getMessage());
            return null;
        }
    }

    // 이벤트 목적 검색
    public List<Event> findByPurpose(User owner, EventPurpose purpose) {
        try {
            List<Event> eventList = eventRepository.findByPurpose(owner, purpose);
            log.info("Event Service | findByPurpose() Success: 검색 성공");
            return eventList;
        }
        catch (EmptyResultDataAccessException e) {
            log.warn("Event Service | findByPurpose() Fail: 검색 결과 없음=> " + e.getMessage());
            return null;
        }
    }

    // 이벤트 평가 검색
    public List<Event> findByEvaluation(User owner, EventEvaluation evaluation) {
        try {
            List<Event> eventList = eventRepository.findByEvaluation(owner, evaluation);
            log.info("Event Service | findByEvaluation() Success: 검색 성공");
            return eventList;
        }
        catch (EmptyResultDataAccessException e) {
            log.warn("Event Service | findByEvaluation() Fail: 검색 결과 없음 => " + e.getMessage());
            return null;
        }
    }

    // 이벤트 정보 수정
    @Transactional
    public boolean updateEvent(Long eventId, String name, LocalDate date, EventPurpose purpose, EventEvaluation evaluation) {
        try {
            Event findEvent = eventRepository.findById(eventId);
            findEvent.updateInfo(name, date, purpose, evaluation);

            em.flush();
            em.clear();

            log.info("Event Service | updateEvent() Success: 수정 성공");
            return true;
        }
        catch(Exception e) {
            log.warn("Event Service | updateEvent() Fail: 에러 발생=> " + e.getMessage());
            return false;
        }
    }

    // 이벤트 삭제
    @Transactional
    public boolean deleteEvent(Event event) {
        try {
            List<Record> recordList = recordRepository.findAll(event);
            List<Friend> friendList = recordList.stream().map(o -> o.getFriend()).collect(Collectors.toList());
            for (Friend friend : friendList) {
                friend.restoreScore(event.getEvaluation());
                friend.calcStatus();
            }
            recordRepository.deleteAllRecords(event);
            eventRepository.deleteEvent(event);

            em.flush();
            em.clear();

            log.info("Event Service | deleteEvent() Success: 삭제 성공");
            return true;
        }
        catch(Exception e) {
            log.warn("Event Service | deleteEvent() Fail: 에러 발생 => " + e.getMessage());
            return false;
        }
    }
}
