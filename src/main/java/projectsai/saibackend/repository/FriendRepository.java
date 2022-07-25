package projectsai.saibackend.repository;

import org.springframework.stereotype.Repository;
import projectsai.saibackend.domain.Friend;
import projectsai.saibackend.domain.Member;
import projectsai.saibackend.domain.enums.RelationStatus;
import projectsai.saibackend.domain.enums.RelationType;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

@Repository
public class FriendRepository {

    @PersistenceContext
    private EntityManager em;

    // CREATE
    @Transactional
    public Long save(Friend friend) {
        em.persist(friend);
        return friend.getId();
    }

    // READ
    public Friend findById(Long id) {
        // owner_id 같이 검색하도록 하는게 안전할까?
        // 그냥 friend_id만 있으면 소유자가 아닌 다른 소유자의 친구 정보를 볼 수도 있잖아?
        return em.find(Friend.class, id);
    }

    public List<Friend> findByName(Member owner, String name) {
        return em.createQuery("select f from Friend f where f.name = :name and f.owner = :owner", Friend.class)
                .setParameter("name", name)
                .setParameter("owner", owner)
                .getResultList();
    }

    public List<Friend> findByType(Member owner, RelationType type) {
        return em.createQuery("select f from Friend f where f.owner = :owner and f.type = :type", Friend.class)
                .setParameter("owner", owner)
                .setParameter("type", type)
                .getResultList();
    }

    public List<Friend> findByStatus(Member owner, RelationStatus status) {
        return em.createQuery("select f from Friend f where f.owner = :owner and f.status = :status", Friend.class)
                .setParameter("owner", owner)
                .setParameter("status", status)
                .getResultList();
    }

    public List<Friend> findAll(Member owner) {
        return em.createQuery("select f from Friend f where f.owner = :owner", Friend.class)
                .setParameter("owner", owner).getResultList();
    }

}
