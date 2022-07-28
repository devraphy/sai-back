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
        return em.find(Member.class, id);
    }

    public Member findByEmail(String email) {
        return em.createQuery("select m from Member m " +
                        "where m.email = :email", Member.class)
                .setParameter("email", email)
                .getSingleResult();
    }

    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }
}
