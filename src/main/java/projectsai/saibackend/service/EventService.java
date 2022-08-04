package projectsai.saibackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import projectsai.saibackend.domain.Event;
import projectsai.saibackend.domain.Friend;
import projectsai.saibackend.domain.Member;
import projectsai.saibackend.domain.enums.EventEvaluation;
import projectsai.saibackend.domain.enums.EventPurpose;
import projectsai.saibackend.repository.EventRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    // 이벤트 저장
    @Transactional
    public Long addEvent(Member member, Event event) {
        return eventRepository.save(member, event);
    }

    // 이벤트 ID 검색
    public Event findById(Long ownerId, Long id) {
        return eventRepository.findById(ownerId, id);
    }

    // 이벤트 전체 검색
    public List<Event> findAll(Long ownerId) {
        return eventRepository.findAll(ownerId);
    }

    // 이벤트 참가자로 검색
    public List<Event> findByParticipants(Long ownerId, List<Friend> friendList) {
        return eventRepository.findByParticipants(ownerId, friendList);
    }

    // 이벤트 이름 검색
    public List<Event> findByName(Long ownerId, String name) {
        return eventRepository.findByEventName(ownerId, name);
    }

    // 이벤트 날짜 검색
    public List<Event> findByDate(Long ownerId, LocalDate date) {
        return eventRepository.findByDate(ownerId, date);
    }

    // 이벤트 목적 검색
    public List<Event> findByPurpose(Long ownerId, EventPurpose purpose) {
        return eventRepository.findByPurpose(ownerId, purpose);
    }

    // 이벤트 평가 검색
    public List<Event> findByEvaluation(Long ownerId, EventEvaluation evaluation) {
        return eventRepository.findByEvaluation(ownerId, evaluation);
    }
}
