package projectsai.saibackend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import projectsai.saibackend.domain.Event;
import projectsai.saibackend.domain.Friend;
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
            Long savedEventId = eventRepository.save(event);
            log.info("addEvent() Success: 이벤트 저장 성공");
            return savedEventId;
        }
        catch (Exception e) {
            log.warn("addEvent() Fail: 이벤트 저장 실패 => " + e.getMessage());
            return null;
        }
    }

    // 이벤트 ID 검색
    public Event findById(Long eventId) {
        try {
            Event event = eventRepository.findById(eventId);
            log.info("findById() Success: eventId로 이벤트 검색 성공");
            return event;
        }
        catch (EmptyResultDataAccessException e) {
            log.warn("findById() Fail: eventId로 이벤트 검색 실패 => " + e.getMessage());
            return null;
        }
    }

    // 이벤트 전체 검색
    public List<Event> findAll(Member owner) {
        try {
            List<Event> eventList = eventRepository.findAll(owner);
            log.info("findAll() Success: 특정 회원의 모든 이벤트 검색 성공");
            return eventList;
        }
        catch (EmptyResultDataAccessException e) {
            log.warn("findAll() Fail: 특정 회원의 모든 이벤트 검색 실패 => " + e.getMessage());
            return null;
        }
    }

    // 이벤트 이름 검색
    public List<Event> findByName(Member owner, String name) {
        try {
            List<Event> eventList = eventRepository.findByEventName(owner, name);
            log.info("findByName() Success: 이벤트 이름으로 이벤트 검색 성공");
            return eventList;
        }
        catch (EmptyResultDataAccessException e) {
            log.warn("findByName() Fail: 이벤트 이름으로 이벤트 검색 실패 => " + e.getMessage());
            return null;
        }
    }

    // 이벤트 날짜 검색
    public List<Event> findByDate(Member owner, LocalDate date) {
        try {
            List<Event> eventList = eventRepository.findByDate(owner, date);
            log.info("findByDate() Success: 이벤트 날짜로 이벤트 검색 성공");
            return eventList;
        }
        catch (EmptyResultDataAccessException e) {
            log.warn("findByDate() Fail: 이벤트 날짜로 이벤트 검색 실패 => " + e.getMessage());
            return null;
        }
    }

    // 이벤트 목적 검색
    public List<Event> findByPurpose(Member owner, EventPurpose purpose) {
        try {
            List<Event> eventList = eventRepository.findByPurpose(owner, purpose);
            log.info("findByPurpose() Success: 이벤트 목적으로 이벤트 검색 성공");
            return eventList;
        }
        catch (EmptyResultDataAccessException e) {
            log.warn("findByPurpose() Fail: 이벤트 목적으로 이벤트 검색 실패 => " + e.getMessage());
            return null;
        }
    }

    // 이벤트 평가 검색
    public List<Event> findByEvaluation(Member owner, EventEvaluation evaluation) {
        try {
            List<Event> eventList = eventRepository.findByEvaluation(owner, evaluation);
            log.info("findByEvaluation() Success: 이벤트 평가로 이벤트 검색 성공");
            return eventList;
        }
        catch (EmptyResultDataAccessException e) {
            log.warn("findByEvaluation() Fail: 이벤트 평가로 이벤트 검색 실패 => " + e.getMessage());
            return null;
        }
    }

    // 이벤트 정보 수정
    public boolean updateEvent(Long eventId, String name, LocalDate date, EventPurpose purpose, EventEvaluation evaluation) {
        try {
            Event findEvent = eventRepository.findById(eventId);
            findEvent.updateInfo(name, date, purpose, evaluation);
            log.info("updateEvent() Success: 이벤트 수정 성공");
            return true;
        }
        catch(Exception e) {
            log.warn("updateEvent() Fail: 이벤트 수정 실패 => " + e.getMessage());
            return false;
        }
    }

    // 이벤트 삭제
    @Transactional
    public boolean deleteEvent(Event event) {
        try {
            eventRepository.deleteEvent(event);
            log.info("deleteEvent() Success: 이벤트 삭제 성공");
            return true;
        }
        catch(Exception e) {
            log.warn("deleteEvent Fail: 이벤트 삭제 실패 => " + e.getMessage());
            return false;
        }
    }
}
