package projectsai.saibackend.repository;

import org.springframework.stereotype.Repository;
import projectsai.saibackend.domain.Event;
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
    public Long addEvent(Event event) {
        em.persist(event);
        return event.getEventId();
    }

    // *********************************** READ화

    // READ - Event ID로 검색
    public Event findById(Long eventId) {
        return em.find(Event.class, eventId);
    }

    // READ - Member ID 검색
    public List<Event> findAll(Member owner) {
        return em.createQuery("select e from Event e " +
                        "where e.owner = :owner", Event.class)
                .setParameter("owner", owner)
                .getResultList();
    }

    // READ - Event 이름으로 검색
    public List<Event> findByEventName(Member owner, String eventName) {
        return em.createQuery("select e from Event e " +
                        "where e.owner = :owner " +
                        "and e.name = :eventName", Event.class)
                .setParameter("owner", owner)
                .setParameter("eventName", eventName)
                .getResultList();
    }

    // READ - 날짜로 검색
    public List<Event> findByDate(Member owner, LocalDate date) {
        return em.createQuery("select e from Event e " +
                        "where e.owner = :owner " +
                        "and e.date = :date", Event.class)
                .setParameter("owner", owner)
                .setParameter("date", date)
                .getResultList();
    }

    // READ - 목적으로 검색
    public List<Event> findByPurpose(Member owner, EventPurpose purpose) {
        return em.createQuery("select e from Event e " +
                                "where e.owner = :owner " +
                                "and e.purpose = :purpose", Event.class)
                .setParameter("owner", owner)
                .setParameter("purpose", purpose)
                .getResultList();
    }

    // READ - 평가로 검색
    public List<Event> findByEvaluation(Member owner, EventEvaluation evaluation) {
        return em.createQuery("select e from Event e " +
                                "where e.owner = :owner " +
                                "and e.evaluation = :evaluation", Event.class)
                .setParameter("owner", owner)
                .setParameter("evaluation", evaluation)
                .getResultList();
    }

    // DELETE - 특정 이벤트 삭제
    public void deleteEvent(Event event) {
        em.remove(event);
    }
}
