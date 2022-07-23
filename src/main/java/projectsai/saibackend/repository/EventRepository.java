package projectsai.saibackend.repository;

import org.springframework.stereotype.Repository;
import projectsai.saibackend.domain.Event;
import projectsai.saibackend.domain.Friend;
import projectsai.saibackend.domain.Member;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class EventRepository {

    @PersistenceContext
    private EntityManager em;

    // CREATE
    public Long save(Event event) {
        em.persist(event);
        return event.getId();
    }

    // READ
    public Event findById(Long id) {
        return em.find(Event.class, id);
    }

    public List<Event> findByName(String name, Member member_id) {
        return em.createQuery("select e from Event e where e.eventName = :name and e.owner = :member_id", Event.class)
                .setParameter("name", name)
                .setParameter("member_id", member_id)
                .getResultList();
    }

    public List<Event> findByFriend(Friend friend_id, Member member_id) {
        return em.createQuery("select e from Event e where e.participants = :friend_id and e.owner = :member_id", Event.class)
                .setParameter("friend_id", friend_id)
                .setParameter("member_id", member_id)
                .getResultList();
    }

    public List<Event> findAll(Member member_id) {
        return em.createQuery("select e from Event e where e.owner = :member_id", Event.class)
                .setParameter("member_id", member_id)
                .getResultList();
    }

}
