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
    public Long save(Member member) {
        em.persist(member);
        return member.getId();
    }

    // *********************************** READ

    // READ - ID로 검색
    public Member findById(Long id) {
        return em.createQuery("select m from Member m " +
                        "where m.id = :id " +
                        "and m.visibility = true", Member.class)
                .setParameter("id", id)
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
                        "where m.visibility = true", Member.class)
                .getResultList();
    }

    // *********************************** UPDATE

    // UPDATE - Member 객체의 전체 속성 수정
    public int updateById(Long id, String name, String email, String password) {
        int result = em.createQuery("update Member as m " +
                        "set m.name = :name, m.email = :email, m.password = :password " +
                        "where m.id = :id")
                .setParameter("name", name)
                .setParameter("email", email)
                .setParameter("password", password)
                .setParameter("id", id)
                .executeUpdate();

        em.clear();

        return result;
    }

    // *********************************** DELETE

    // DELETE - visibility를 수정하여 검색이 되지 않도록 함.
    public int deleteByEmail(String email) {
        int result = em.createQuery("update Member as m set m.visibility = :visibility where m.email = :email")
                .setParameter("visibility", Boolean.FALSE)
                .setParameter("email", email)
                .executeUpdate();

        em.clear();

        return result;
    }
}
