package projectsai.saibackend.repository;

import org.springframework.stereotype.Repository;
import projectsai.saibackend.domain.Friend;
import projectsai.saibackend.domain.Member;
import projectsai.saibackend.domain.enums.RelationStatus;
import projectsai.saibackend.domain.enums.RelationType;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.List;

@Repository
public class FriendRepository {

    @PersistenceContext
    private EntityManager em;

    // CREATE
    public Long save(Member owner, Friend friend) {
        owner.addFriend(friend);
        em.persist(owner);
        return friend.getId();
    }

    // READ
    public List<Friend> findAll(Long ownerId) { // Member ID 값을 이용한 검색
        return em.createQuery("select f from Friend f " +
                        "where f.owner.id = :ownerId", Friend.class)
                .setParameter("ownerId", ownerId)
                .getResultList();
    }

    public Friend findById(Long ownerId, Long id) { // Member ID 값과 Friend ID를 이용한 검색
        return em.createQuery("select f from Friend f " +
                        "where f.owner.id = :ownerId " +
                        "and f.id = :id", Friend.class)
                .setParameter("ownerId", ownerId)
                .setParameter("id", id)
                .getSingleResult();
    }

    public List<Friend> findByName(Long ownerId, String name) { // Member ID, 친구의 이름을 이용한 검색
        return em.createQuery("select f from Friend f " +
                        "where f.owner.id = :ownerId " +
                        "and f.name = :name ", Friend.class)
                .setParameter("ownerId", ownerId)
                .setParameter("name", name)
                .getResultList();
    }

    public List<Friend> findByType(Long ownerId, RelationType type) { // Member ID, 관계 종류를 이용한 검색
        return em.createQuery("select f from Friend f " +
                        "where f.owner.id = :ownerId " +
                        "and f.type = :type", Friend.class)
                .setParameter("ownerId", ownerId)
                .setParameter("type", type)
                .getResultList();
    }

    public List<Friend> findByStatus(Long ownerId, RelationStatus status) { // Member ID, 관계 상태를 이용한 검색
        return em.createQuery("select f from Friend f " +
                        "where f.owner.id = :ownerId " +
                        "and f.status = :status", Friend.class)
                .setParameter("ownerId", ownerId)
                .setParameter("status", status)
                .getResultList();
    }

    public int updateById(Long ownerId, Long friendId, String name, LocalDate birthDate, String memo, RelationType friendType) {
        int result = em.createQuery("update Friend f " +
                        "set f.name = :name, f.birthDate = :birthDate, f.memo = :memo, f.type = :friendType " +
                        "where f.owner.id = :ownerId and f.id = :friendId")
                .setParameter("name", name)
                .setParameter("birthDate", birthDate)
                .setParameter("memo", memo)
                .setParameter("friendType", friendType)
                .setParameter("ownerId", ownerId)
                .setParameter("friendId", friendId)
                .executeUpdate();

        em.clear();

        return result;
    }

    public int deleteById(Long ownerId, Long friendId) {
        int result = em.createQuery("delete from Friend f " +
                        "where f.owner.id = :ownerId " +
                        "and f.id = :friendId")
                .setParameter("ownerId", ownerId)
                .setParameter("friendId", friendId)
                .executeUpdate();

        em.clear();

        return result;
    }
}
