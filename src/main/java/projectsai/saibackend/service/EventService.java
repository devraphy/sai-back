package projectsai.saibackend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import projectsai.saibackend.domain.Event;
import projectsai.saibackend.domain.Member;
import projectsai.saibackend.domain.enums.EventEvaluation;
import projectsai.saibackend.domain.enums.EventPurpose;
import projectsai.saibackend.repository.EventRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.List;

@Service @Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EventService {

    @PersistenceContext EntityManager em;
    private final EventRepository eventRepository;

    // 이벤트 저장
    @Transactional
    public Long addEvent(Event event) {
        try {
            Long savedEventId = eventRepository.addEvent(event);
            log.info("Event | addEvent() Success: 저장 성공");
            return savedEventId;
        }
        catch (Exception e) {
            log.warn("Event | addEvent() Fail: 에러 발생 => " + e.getMessage());
            return null;
        }
    }

    // 이벤트 ID 검색
    public Event findById(Long eventId) {
        try {
            Event event = eventRepository.findById(eventId);
            log.info("Event | findById() Success: 검색 성공");
            return event;
        }
        catch (EmptyResultDataAccessException e) {
            log.warn("Event | findById() Fail: 검색 결과 없음 => " + e.getMessage());
            return null;
        }
    }

    // 이벤트 전체 검색
    public List<Event> findAll(Member owner) {
        try {
            List<Event> eventList = eventRepository.findAll(owner);
            log.info("Event | findAll() Success: 검색 성공");
            return eventList;
        }
        catch (EmptyResultDataAccessException e) {
            log.warn("Event | findAll() Fail: 검색 결과 없음 => " + e.getMessage());
            return null;
        }
    }

    // 이벤트 이름 검색
    public List<Event> findByName(Member owner, String name) {
        try {
            List<Event> eventList = eventRepository.findByEventName(owner, name);
            log.info("Event | findByName() Success: 검색 성공");
            return eventList;
        }
        catch (EmptyResultDataAccessException e) {
            log.warn("Event | findByName() Fail: 검색 결과 없음 => " + e.getMessage());
            return null;
        }
    }

    // 이벤트 날짜 검색
    public List<Event> findByDate(Member owner, LocalDate date) {
        try {
            List<Event> eventList = eventRepository.findByDate(owner, date);
            log.info("Event | findByDate() Success: 검색 성공");
            return eventList;
        }
        catch (EmptyResultDataAccessException e) {
            log.warn("Event | findByDate() Fail: 검색 결과 없음 => " + e.getMessage());
            return null;
        }
    }

    // 이벤트 목적 검색
    public List<Event> findByPurpose(Member owner, EventPurpose purpose) {
        try {
            List<Event> eventList = eventRepository.findByPurpose(owner, purpose);
            log.info("Event | findByPurpose() Success: 검색 성공");
            return eventList;
        }
        catch (EmptyResultDataAccessException e) {
            log.warn("Event | findByPurpose() Fail: 검색 결과 없음=> " + e.getMessage());
            return null;
        }
    }

    // 이벤트 평가 검색
    public List<Event> findByEvaluation(Member owner, EventEvaluation evaluation) {
        try {
            List<Event> eventList = eventRepository.findByEvaluation(owner, evaluation);
            log.info("Event | findByEvaluation() Success: 검색 성공");
            return eventList;
        }
        catch (EmptyResultDataAccessException e) {
            log.warn("Event | findByEvaluation() Fail: 검색 결과 없음 => " + e.getMessage());
            return null;
        }
    }

    // 이벤트 정보 수정
    public boolean updateEvent(Long eventId, String name, LocalDate date, EventPurpose purpose, EventEvaluation evaluation) {
        try {
            Event findEvent = eventRepository.findById(eventId);
            findEvent.updateInfo(name, date, purpose, evaluation);

            em.flush();
            em.clear();

            log.info("Event | updateEvent() Success: 수정 성공");
            return true;
        }
        catch(Exception e) {
            log.warn("Event | updateEvent() Fail: 에러 발생=> " + e.getMessage());
            return false;
        }
    }

    // 이벤트 삭제
    @Transactional
    public boolean deleteEvent(Event event) {
        try {
            eventRepository.deleteEvent(event);
            log.info("Event | deleteEvent() Success: 삭제 성공");
            return true;
        }
        catch(Exception e) {
            log.warn("Event | deleteEvent() Fail: 에러 발생 => " + e.getMessage());
            return false;
        }
    }
}
