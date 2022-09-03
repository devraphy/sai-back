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

    @PersistenceContext
    EntityManager em;

    public Long addRecord(Record record) {
        em.persist(record);
        return record.getRecordId();
    }

    public Record findById(Long recordId) {
        return em.find(Record.class, recordId);
    }

    public List<Record> findAll(Event event) {
        return em.createQuery("select r from Record r " +
                        "where r.event = :event", Record.class)
                .setParameter("event", event)
                .getResultList();
    }

    public List<Record> findByParticipant(Friend friend) {
        return em.createQuery("select r from Record r " +
                        "where r.friend = :friend", Record.class)
                .setParameter("friend", friend)
                .getResultList();
    }

    public Record findOne(Event event, Friend friend) {
        return em.createQuery("select r from Record r " +
                        "where r.event = :event " +
                        "and r.friend = :friend", Record.class)
                .setParameter("event", event)
                .setParameter("friend", friend)
                .getSingleResult();
    }

    public int deleteAllRecords(Event event) {
        int result = em.createQuery("delete from Record r where r.event = :event")
                .setParameter("event", event)
                .executeUpdate();
        return result;
    }

    public void deleteRecord(Record record) {
        em.remove(record);
    }
}
