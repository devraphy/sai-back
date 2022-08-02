package projectsai.saibackend.repository;

import org.springframework.stereotype.Repository;
import projectsai.saibackend.domain.Member;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class MemberRepository {

    @PersistenceContext
    private EntityManager em;

    // CREATE
    public Long save(Member member) {
        em.persist(member);
        return member.getId();
    }

    // READ
    public Member findById(Long id) {
        return em.createQuery("select m from Member m " +
                        "where m.id = :id " +
                        "and m.visibility = true", Member.class)
                .setParameter("id", id)
                .getSingleResult();
    }

    public Member findByEmail(String email) {
        return em.createQuery("select m from Member m " +
                        "where m.email = :email", Member.class)
                .setParameter("email", email)
                .getSingleResult();
    }

    public List<Member> findAll() {
        return em.createQuery("select m from Member m " +
                        "where m.visibility = true", Member.class)
                .getResultList();
    }

    // UPDATE
    public int updateById(Long id, String name, String email, String password) {
        return em.createQuery("update Member as m " +
                        "set m.name = :name, m.email = :email, m.password = :password " +
                        "where m.id = :id")
                .setParameter("name", name)
                .setParameter("email", email)
                .setParameter("password", password)
                .setParameter("id", id)
                .executeUpdate();
    }

    // DELETE
    public int deleteByEmail(String email) {
        return em.createQuery("update Member as m set m.visibility = :visibility where m.email = :email")
                .setParameter("visibility", Boolean.FALSE)
                .setParameter("email", email)
                .executeUpdate();
    }
}
