package projectsai.saibackend.repository;

import org.springframework.stereotype.Repository;
import projectsai.saibackend.domain.Role;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class RoleRepository  {

    @PersistenceContext
    private EntityManager em;

    public Long addRole(Role role) {
        em.persist(role);
        return role.getId();
    }

    public Role findByPosition(String position) {
        return em.createQuery("select r from Role r " +
                        "where r.position = :position", Role.class)
                .setParameter("position", position)
                .getSingleResult();
    }
}
