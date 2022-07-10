package projectsai.saibackend.repository;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class EventRepository {

    @PersistenceContext
    private EntityManager em;


}
