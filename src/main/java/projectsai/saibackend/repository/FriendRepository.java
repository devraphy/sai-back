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
    public Long save(Member owner, Friend friend) {
        owner.addFriend(friend);
        em.persist(owner);
        return friend.getId();
    }

    // *********************************** READ

    // READ - Member ID로 검색
    public List<Friend> findAll(Long ownerId) {
        return em.createQuery("select f from Friend f " +
                        "where f.owner.id = :ownerId", Friend.class)
                .setParameter("ownerId", ownerId)
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
    public List<Friend> findByName(Long ownerId, String name) {
        return em.createQuery("select f from Friend f " +
                        "where f.owner.id = :ownerId " +
                        "and f.name = :name ", Friend.class)
                .setParameter("ownerId", ownerId)
                .setParameter("name", name)
                .getResultList();
    }

    // READ - 관계 종류로 검색
    public List<Friend> findByType(Long ownerId, RelationType type) {
        return em.createQuery("select f from Friend f " +
                        "where f.owner.id = :ownerId " +
                        "and f.type = :type", Friend.class)
                .setParameter("ownerId", ownerId)
                .setParameter("type", type)
                .getResultList();
    }

    // READ - 관계 상태로 검색
    public List<Friend> findByStatus(Long ownerId, RelationStatus status) {
        return em.createQuery("select f from Friend f " +
                        "where f.owner.id = :ownerId " +
                        "and f.status = :status", Friend.class)
                .setParameter("ownerId", ownerId)
                .setParameter("status", status)
                .getResultList();
    }

    // READ - 다수의 Friend ID로 다중 검색
    public List<Friend> findFriends(Long ownerId, List<Long> friendIds) {
        return em.createQuery("select f from Friend f " +
                        "where f.owner.id = :ownerId " +
                        "and f.id in :friendIds", Friend.class)
                .setParameter("ownerId", ownerId)
                .setParameter("friendIds", friendIds)
                .getResultList();
    }
}