package projectsai.saibackend.repository;

import org.springframework.stereotype.Repository;
import projectsai.saibackend.domain.Friend;
import projectsai.saibackend.domain.Member;
import projectsai.saibackend.domain.enums.RelationStatus;
import projectsai.saibackend.domain.enums.RelationType;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class FriendRepository {

    @PersistenceContext
    private EntityManager em;

    // *********************************** CREATE

    // CREATE - Friend 객체 영속화
    public Long addFriend(Friend friend) {
        em.persist(friend);
        return friend.getId();
    }

    // *********************************** READ

    // READ - Member ID로 검색
    public List<Friend> findAll(Member owner) {
        return em.createQuery("select f from Friend f " +
                        "where f.owner = :owner", Friend.class)
                .setParameter("owner", owner)
                .getResultList();
    }

    // READ - 단일 Friend ID로 검색
    public Friend findById(Long friendId) {
        return em.createQuery("select f from Friend f " +
                        "where f.id = :friendId", Friend.class)
                .setParameter("friendId", friendId)
                .getSingleResult();
    }

    // READ - 이름으로 검색
    public List<Friend> findByName(Member owner, String name) {
        return em.createQuery("select f from Friend f " +
                        "where f.owner = :owner " +
                        "and f.name = :name ", Friend.class)
                .setParameter("owner", owner)
                .setParameter("name", name)
                .getResultList();
    }

    // READ - 관계 종류로 검색
    public List<Friend> findByType(Member owner, RelationType type) {
        return em.createQuery("select f from Friend f " +
                        "where f.owner = :owner " +
                        "and f.type = :type", Friend.class)
                .setParameter("owner", owner)
                .setParameter("type", type)
                .getResultList();
    }

    // READ - 관계 상태로 검색
    public List<Friend> findByStatus(Member owner, RelationStatus status) {
        return em.createQuery("select f from Friend f " +
                        "where f.owner = :owner " +
                        "and f.status = :status", Friend.class)
                .setParameter("owner", owner)
                .setParameter("status", status)
                .getResultList();
    }

    // READ - 다수의 Friend ID로 다중 검색
    public List<Friend> findByIds(List<Long> friendIds) {
        return em.createQuery("select f from Friend f " +
                        "where f.id in :friendIds", Friend.class)
                .setParameter("friendIds", friendIds)
                .getResultList();
    }

    public void deleteFriend(Friend friend) {
        em.remove(friend);
        em.flush();
        em.clear();
    }
}