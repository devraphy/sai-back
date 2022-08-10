package projectsai.saibackend.repository;

import org.springframework.stereotype.Repository;
import projectsai.saibackend.domain.Event;
import projectsai.saibackend.domain.Friend;
import projectsai.saibackend.domain.Record;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class RecordRepository {

    @PersistenceContext EntityManager em;

    public Long save(Record record) {
        em.persist(record);
        return record.getId();
    }

    public List<Record> findAllParticipants(Event event) {
        return em.createQuery("select r from Record r where r.event = :event", Record.class)
                .setParameter("event", event)
                .getResultList();
    }

    public List<Record> findByParticipant(Friend friend) {
        return em.createQuery("select r from Record r where r.friend = :friend", Record.class)
                .setParameter("friend", friend)
                .getResultList();
    }

    public Record findOneRecord(Event event, Friend friend) {
        return em.createQuery("select r from Record r where r.event = :event and r.friend = :friend", Record.class)
                .setParameter("event", event)
                .setParameter("friend", friend)
                .getSingleResult();
    }
}
