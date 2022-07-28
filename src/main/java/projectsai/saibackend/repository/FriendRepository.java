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

    // CREATE
    public Long save(Member owner, Friend friend) {
        owner.addFriend(friend);
        em.persist(owner);
        return friend.getId();
    }

    // READ
    public List<Friend> findAll(Member owner) { // Member ID 값을 이용한 검색
        return em.createQuery("select f from Friend f where f.owner = :owner", Friend.class)
                .setParameter("owner", owner)
                .getResultList();
    }

    public List<Friend> findByName(Member owner, String name) { // Member ID, 친구의 이름을 이용한 검색
        return em.createQuery("select f from Friend f where f.owner = :owner and f.name = :name ", Friend.class)
                .setParameter("owner", owner)
                .setParameter("name", name)
                .getResultList();
    }

    public List<Friend> findByType(Member owner, RelationType type) { // Member ID, 관계 종류를 이용한 검색
        return em.createQuery("select f from Friend f where f.owner = :owner and f.type = :type", Friend.class)
                .setParameter("owner", owner)
                .setParameter("type", type)
                .getResultList();
    }

    public List<Friend> findByStatus(Member owner, RelationStatus status) { // Member ID, 관계 상태를 이용한 검색
        return em.createQuery("select f from Friend f where f.owner = :owner and f.status = :status", Friend.class)
                .setParameter("owner", owner)
                .setParameter("status", status)
                .getResultList();
    }
}
