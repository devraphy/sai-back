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

    // CREATE
    public Long save(Member owner, Event event) {
        owner.addEvent(event);
        em.persist(owner);
        return event.getId();
    }

    // READ

    public Event findById(Long ownerId, Long eventId) {
        return em.createQuery("select e from Event e " +
                        "where e.owner.id = :ownerId " +
                        "and e.id = :eventId", Event.class)
                .setParameter("ownerId", ownerId)
                .setParameter("eventId", eventId)
                .getSingleResult();
    }

    public List<Event> findAll(Long ownerId) {
        return em.createQuery("select e from Event e " +
                        "where e.owner.id = :ownerId", Event.class)
                .setParameter("ownerId", ownerId)
                .getResultList();
    }

    public List<Event> findByParticipants(Long ownerId, List<Friend> friendList) {
        return em.createQuery("select e from Event e " +
                        "join e.participants p " +
                        "where e.owner.id = :ownerId " +
                        "and p in :friendList", Event.class)
                .setParameter("ownerId", ownerId)
                .setParameter("friendList", friendList)
                .getResultList();
    }

    public List<Event> findByEventName(Long ownerId, String eventName) {
        return em.createQuery("select e from Event e " +
                        "where e.owner.id = :ownerId " +
                        "and e.name = :eventName", Event.class)
                .setParameter("ownerId", ownerId)
                .setParameter("eventName", eventName)
                .getResultList();
    }

    public List<Event> findByDate(Long ownerId, LocalDate date) {
        return em.createQuery("select e from Event e " +
                        "where e.owner.id = :ownerId " +
                        "and e.date = :date", Event.class)
                .setParameter("ownerId", ownerId)
                .setParameter("date", date)
                .getResultList();
    }

    public List<Event> findByPurpose(Long ownerId, EventPurpose purpose) {
        return em.createQuery("select e from Event e " +
                                "where e.owner.id = :ownerId " +
                                "and e.purpose = :purpose", Event.class)
                .setParameter("ownerId", ownerId)
                .setParameter("purpose", purpose)
                .getResultList();
    }

    public List<Event> findByEvaluation(Long ownerId, EventEvaluation evaluation) {
        return em.createQuery("select e from Event e " +
                                "where e.owner.id = :ownerId " +
                                "and e.evaluation = :evaluation", Event.class)
                .setParameter("ownerId", ownerId)
                .setParameter("evaluation", evaluation)
                .getResultList();
    }
}
