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
        return em.createQuery("select u from Member u " +
                        "where u.id = :memberId " +
                        "and u.visibility = :visibility", Member.class)
                .setParameter("memberId", memberId)
                .setParameter("visibility", 1)
                .getSingleResult();
    }

    // READ - email로 검색
    public Member findByEmail(String email) {
        return em.createQuery("select u from Member u " +
                        "where u.email = :email", Member.class)
                .setParameter("email", email)
                .getSingleResult();
    }

    // READ - 전체 검색
    public List<Member> findAll() {
        return em.createQuery("select u from Member u " +
                        "where u.visibility = :visibility", Member.class)
                .setParameter("visibility", 1)
                .getResultList();
    }
}
