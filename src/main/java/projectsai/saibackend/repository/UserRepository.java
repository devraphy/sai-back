package projectsai.saibackend.repository;

import org.springframework.stereotype.Repository;
import projectsai.saibackend.domain.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class UserRepository {

    @PersistenceContext
    private EntityManager em;

    // *********************************** CREATE

    // CREATE - User 객체 영속화
    public Long addMember(User user) {
        em.persist(user);
        return user.getUserId();
    }

    // *********************************** READ

    // READ - ID로 검색
    public User findById(Long memberId) {
        return em.createQuery("select u from User u " +
                        "where u.id = :memberId " +
                        "and u.visibility = :visibility", User.class)
                .setParameter("memberId", memberId)
                .setParameter("visibility", 1)
                .getSingleResult();
    }

    // READ - email로 검색
    public User findByEmail(String email) {
        return em.createQuery("select u from User u " +
                        "where u.email = :email", User.class)
                .setParameter("email", email)
                .getSingleResult();
    }

    // READ - 전체 검색
    public List<User> findAll() {
        return em.createQuery("select u from User u " +
                        "where u.visibility = :visibility", User.class)
                .setParameter("visibility", 1)
                .getResultList();
    }
}
