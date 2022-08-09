package projectsai.saibackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import projectsai.saibackend.domain.Event;
import projectsai.saibackend.domain.Friend;
import projectsai.saibackend.domain.Member;
import projectsai.saibackend.domain.enums.EventEvaluation;
import projectsai.saibackend.domain.enums.EventPurpose;
import projectsai.saibackend.repository.EventRepository;
import projectsai.saibackend.repository.FriendRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EventService {

    @PersistenceContext EntityManager em;
    private final EventRepository eventRepository;
    private final FriendRepository friendRepository;

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

    // 이벤트 정보 수정
//    public boolean updateEvent(Long eventId, String name, LocalDate date, EventEvaluation evaluation, List<Friend> participants) {
//        try {
//            Event findEvent = eventRepository.findById(eventId);
//            findEvent.updateInfo(name, date, evaluation, participants);
//        } catch(EmptyResultDataAccessException e) {
//            return false;
//        }
//        return true;
//    }

//    public boolean deleteEvent(Long eventId, Long friendId) {
//        Friend findFriend = friendRepository.findById(friendId);
//        Event findEvent = eventRepository.findByParticipant(eventId, findFriend);
//        System.out.println(findEvent.getName());
//        em.remove(findEvent);
//        em.flush();
//        em.clear();
//
////        int result = em.createQuery("delete from Event e where e.owner.id = :ownerId and e.participants = :friendId")
////                    .setParameter("ownerId", ownerId)
////                    .setParameter("friendId", friendId)
////                    .executeUpdate();
//        return true;
//    }
}
