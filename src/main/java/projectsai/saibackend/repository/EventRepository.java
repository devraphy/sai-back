package projectsai.saibackend.repository;

import org.springframework.stereotype.Repository;
import projectsai.saibackend.domain.Event;
import projectsai.saibackend.domain.Friend;
import projectsai.saibackend.domain.Member;
import projectsai.saibackend.domain.enums.EventEvaluation;
import projectsai.saibackend.domain.enums.EventPurpose;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.List;

@Repository
public class EventRepository {

    @PersistenceContext
    private EntityManager em;

    // *********************************** CREATE

    // CREATE - 새로운 Event 객체 영속화
    public Long save(Member owner, Event event) {
        owner.addEvent(event);
        em.persist(owner);
        return event.getId();
    }

    // *********************************** READ화

    // READ - Event ID로 검색
    public Event findById(Long eventId) {
        return em.createQuery("select e from Event e " +
                        "where e.id = :eventId", Event.class)
                .setParameter("eventId", eventId)
                .getSingleResult();
    }

    // READ - Member ID로 검색
    public List<Event> findAll(Long ownerId) {
        return em.createQuery("select e from Event e " +
                        "where e.owner.id = :ownerId", Event.class)
                .setParameter("ownerId", ownerId)
                .getResultList();
    }

    // READ - 다중 참가자로 검색
    public List<Event> findByParticipants(Long ownerId, List<Friend> friendList) {
        return em.createQuery("select distinct e from Event e " +
                        "join e.participants p " +
                        "where e.owner.id = :ownerId " +
                        "and p in :friendList", Event.class)
                .setParameter("ownerId", ownerId)
                .setParameter("friendList", friendList)
                .getResultList();
    }

    // READ - 단일 참가자로 검색
    public Event findByParticipant(Long eventId, Friend friend) {
        return em.createQuery("select distinct e from Event e " +
                        "join e.participants p " +
                        "where e.id = :eventId " +
                        "and p = :friend", Event.class)
                .setParameter("eventId", eventId)
                .setParameter("friend", friend)
                .getSingleResult();
    }

    // READ - Event 이름으로 검색
    public List<Event> findByEventName(Long ownerId, String eventName) {
        return em.createQuery("select e from Event e " +
                        "where e.owner.id = :ownerId " +
                        "and e.name = :eventName", Event.class)
                .setParameter("ownerId", ownerId)
                .setParameter("eventName", eventName)
                .getResultList();
    }

    // READ - 날짜로 검색
    public List<Event> findByDate(Long ownerId, LocalDate date) {
        return em.createQuery("select e from Event e " +
                        "where e.owner.id = :ownerId " +
                        "and e.date = :date", Event.class)
                .setParameter("ownerId", ownerId)
                .setParameter("date", date)
                .getResultList();
    }

    // READ - 목적으로 검색
    public List<Event> findByPurpose(Long ownerId, EventPurpose purpose) {
        return em.createQuery("select e from Event e " +
                                "where e.owner.id = :ownerId " +
                                "and e.purpose = :purpose", Event.class)
                .setParameter("ownerId", ownerId)
                .setParameter("purpose", purpose)
                .getResultList();
    }

    // READ - 평가로 검색
    public List<Event> findByEvaluation(Long ownerId, EventEvaluation evaluation) {
        return em.createQuery("select e from Event e " +
                                "where e.owner.id = :ownerId " +
                                "and e.evaluation = :evaluation", Event.class)
                .setParameter("ownerId", ownerId)
                .setParameter("evaluation", evaluation)
                .getResultList();
    }
}
