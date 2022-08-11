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
        return eventRepository.save(event);
    }

    // 이벤트 ID 검색
    public Event findById(Long eventId) {
        return eventRepository.findById(eventId);
    }

    // 이벤트 전체 검색
    public List<Event> findAll(Member owner) {
        return eventRepository.findAll(owner);
    }

    // 이벤트 이름 검색
    public List<Event> findByName(Member owner, String name) {
        return eventRepository.findByEventName(owner, name);
    }

    // 이벤트 날짜 검색
    public List<Event> findByDate(Member owner, LocalDate date) {
        return eventRepository.findByDate(owner, date);
    }

    // 이벤트 목적 검색
    public List<Event> findByPurpose(Member owner, EventPurpose purpose) {
        return eventRepository.findByPurpose(owner, purpose);
    }

    // 이벤트 평가 검색
    public List<Event> findByEvaluation(Member owner, EventEvaluation evaluation) {
        return eventRepository.findByEvaluation(owner, evaluation);
    }

    // 이벤트 정보 수정
    public boolean updateEvent(Long eventId, String name, LocalDate date, EventPurpose purpose, EventEvaluation evaluation) {
        try {
            Event findEvent = eventRepository.findById(eventId);
            findEvent.updateInfo(name, date, purpose, evaluation);
        } catch(EmptyResultDataAccessException e) {
            return false;
        }
        return true;
    }

    @Transactional
    public boolean deleteEvent(Event event) {
        try {
            eventRepository.deleteEvent(event);
        } catch(Exception e) {
            log.warn("deleteEvent Fail: 이벤트 삭제 실패 => " + e.getMessage());
            return false;
        }
        return true;
    }
}
