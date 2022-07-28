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
    public Event findById(Member member, Long id) {
        return eventRepository.findById(member, id);
    }

    // 이벤트 전체 검색
    public List<Event> findAll(Member member) {
        return eventRepository.findAll(member);
    }

    // 이벤트 참가자로 검색
    public List<Event> findByParticipants(Member member, List<Friend> friendList) {
        return eventRepository.findByParticipants(member, friendList);
    }

    // 이벤트 이름 검색
    public List<Event> findByName(Member member, String name) {
        return eventRepository.findByEventName(member, name);
    }

    // 이벤트 날짜 검색
    public List<Event> findByDate(Member member, LocalDate date) {
        return eventRepository.findByDate(member, date);
    }

    // 이벤트 목적 검색
    public List<Event> findByPurpose(Member member, EventPurpose purpose) {
        return eventRepository.findByPurpose(member, purpose);
    }

    // 이벤트 평가 검색
    public List<Event> findByEvaluation(Member member, EventEvaluation evaluation) {
        return eventRepository.findByEvaluation(member, evaluation);
    }
}
