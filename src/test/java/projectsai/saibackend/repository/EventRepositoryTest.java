package projectsai.saibackend.repository;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import projectsai.saibackend.domain.Event;
import projectsai.saibackend.domain.Friend;
import projectsai.saibackend.domain.Member;
import projectsai.saibackend.domain.enums.EventEvaluation;
import projectsai.saibackend.domain.enums.EventPurpose;
import projectsai.saibackend.domain.enums.RelationStatus;
import projectsai.saibackend.domain.enums.RelationType;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest @Slf4j @Transactional
class EventRepositoryTest {

    @Autowired EventRepository eventRepository;
    @PersistenceContext EntityManager em;

    private Friend friend1, friend2, business1;
    private Event event1, event2, event3;
    private Member owner;

    @BeforeEach
    public void createMemberAndFriend() {
        owner = new Member("이근형","abc@gmail.com", "abcdefg", LocalDate.now());
        friend1 = new Friend("친구1", RelationType.FRIEND, RelationStatus.NORMAL, 50, null, null, null);
        friend2 = new Friend("친구2", RelationType.FRIEND, RelationStatus.POSITIVE, 80, null, null, null);
        business1 = new Friend("동료1", RelationType.BUSINESS, RelationStatus.NORMAL, 50, null, null, null);
        owner.addFriend(friend1);
        owner.addFriend(friend2);
        owner.addFriend(business1);
        em.persist(owner);
    }

    public List<Friend> findFriends() {
        return em.createQuery("select f from Friend f where f.owner =: owner and f.type = :relationType", Friend.class)
                .setParameter("owner", this.owner)
                .setParameter("relationType", RelationType.FRIEND)
                .getResultList();
    }

    public List<Friend> findBusiness() {
        return em.createQuery("select f from Friend f where f.owner =: owner and f.type = :relationType", Friend.class)
                .setParameter("owner", this.owner)
                .setParameter("relationType", RelationType.BUSINESS)
                .getResultList();
    }


    @Test @DisplayName("모든 Event 검색")
    @Rollback(false)
    void findAll() {
        //given
        List<Friend> friendList = findFriends();
        event1 = new Event(LocalDate.now(), EventPurpose.CHILL, "친구 모임", EventEvaluation.NORMAL, friendList);
        owner.addEvent(event1);
        em.flush();
        em.clear();

        //when
        List<Event> eventList = eventRepository.findAll(owner);

        //then
        for(Event event : eventList) {
            for(Friend participant : event.getParticipants()) {
                Assertions.assertThat(participant.getName()).isIn(friend1.getName(), friend2.getName());
            }
        }
    }

    @Test @DisplayName("참가자로 Event 검색")
    void findByFriend() {
        //given
        //when
        //then
    }

    @Test @DisplayName("Event 이름으로 검색")
    void findByEventName() {
        //given
        //when
        //then
    }

    @Test @DisplayName("Event 날짜로 검색")
    void findByDate() {
        //given
        //when
        //then
    }

    @Test @DisplayName("Event 목적으로 검색")
    void findByPurpose() {
        //given
        //when
        //then
    }

    @Test @DisplayName("Event 평가로 검색")
    void findByEvaluation() {
        //given
        //when
        //then
    }
}