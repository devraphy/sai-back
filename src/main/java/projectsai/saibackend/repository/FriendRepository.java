package projectsai.saibackend.repository;

import org.springframework.stereotype.Repository;
import projectsai.saibackend.domain.Friend;
import projectsai.saibackend.domain.enums.RelationStatus;
import projectsai.saibackend.domain.enums.Relationship;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class FriendRepository {

    @PersistenceContext
    private EntityManager em;

    //CREATE
    public Long save(Friend friend) {
        em.persist(friend);
        return friend.getId();
    }

    //SEARCH
    public Friend findById(Long id) {
        // owner_id 같이 검색하도록 하는게 안전할까?
        // 그냥 friend_id만 있으면 소유자가 아닌 다른 소유자의 친구 정보를 볼 수도 있잖아?
        return em.find(Friend.class, id);
    }

    public List<Friend> findAll(Long memberId) {
        return em.createQuery("select f from Friend f where f.owner = :memberId", Friend.class)
                .setParameter("memberId", memberId).getResultList();
    }

    public List<Friend> findByName(Long memberId, String name) {
        return em.createQuery("select f from Friend f where f.name = :name and f.owner = :memberId", Friend.class)
                .setParameter("name", name)
                .setParameter("memberId", memberId)
                .getResultList();
    }

    public List<Friend> findByRelationship(Long memberId, Relationship relationship) {
        return em.createQuery("select f from Friend f where f.owner = :memberId and f.relationship = :relationship", Friend.class)
                .setParameter("memberId", memberId)
                .setParameter("relationship", relationship)
                .getResultList();
    }

    public List<Friend> findByStatus(Long memberId, RelationStatus status) {
        return em.createQuery("select f from Friend f where f.owner = :memberId and f.relationStatus = :status", Friend.class)
                .setParameter("memberId", memberId)
                .setParameter("status", status)
                .getResultList();
    }

}
