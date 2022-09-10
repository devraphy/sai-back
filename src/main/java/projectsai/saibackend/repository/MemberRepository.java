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

    // *********************************** CREATE

    // CREATE - Member 객체 영속화
    public Long addMember(Member user) {
        em.persist(user);
        return user.getMemberId();
    }

    // *********************************** READ

    // READ - ID로 검색
    public Member findById(Long memberId) {
        return em.createQuery("select m from Member m " +
                        "where m.id = :memberId " +
                        "and m.visibility = :visibility", Member.class)
                .setParameter("memberId", memberId)
                .setParameter("visibility", Boolean.TRUE)
                .getSingleResult();
    }

    // READ - email로 검색
    public Member findByEmail(String email) {
        return em.createQuery("select m from Member m " +
                        "where m.email = :email", Member.class)
                .setParameter("email", email)
                .getSingleResult();
    }

    // READ - 전체 검색
    public List<Member> findAll() {
        return em.createQuery("select m from Member m " +
                        "where m.visibility = :visibility", Member.class)
                .setParameter("visibility", Boolean.TRUE)
                .getResultList();
    }
}
