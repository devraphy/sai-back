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

    // READ
    public List<Event> findAll(Member owner) {
        return em.createQuery("select e from Event e where e.owner = :owner", Event.class)
                .setParameter("owner", owner)
                .getResultList();
    }

    public List<Event> findByFriend(Member owner, List<Friend> friendList) { // 이거 제대로 동작하는지 검증하기
        return em.createQuery("select e from Event e where e.owner = :owner and e.participants = :friendList", Event.class)
                .setParameter("owner", owner)
                .setParameter("friendList", friendList)
                .getResultList();
    }

    public List<Event> findByEventName(Member owner, String eventName) {
        return em.createQuery("select e from Event e where e.owner = :owner and e.eventName = :eventName", Event.class)
                .setParameter("owner", owner)
                .setParameter("eventName", eventName)
                .getResultList();
    }

    public List<Event> findByDate(Member owner, LocalDate date) {
        return em.createQuery("select e from Event e where e.owner = :owner and e.eventDate = :date", Event.class)
                .setParameter("owner", owner)
                .setParameter("date", date)
                .getResultList();
    }

    public List<Event> findByPurpose(Member owner, EventPurpose purpose) {
        return em.createQuery("select e from Event e where e.owner = :owner and e.eventPurpose = :purpose", Event.class)
                .setParameter("owner", owner)
                .setParameter("purpose", purpose)
                .getResultList();
    }

    public List<Event> findByEvaluation(Member owner, EventEvaluation evaluation) {
        return em.createQuery("select e from Event e where e.owner = :owner and e.evaluation = :evaluation", Event.class)
                .setParameter("owner", owner)
                .setParameter("evaluation", evaluation)
                .getResultList();
    }
}
