package projectsai.saibackend.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import projectsai.saibackend.domain.Event;
import projectsai.saibackend.domain.Friend;
import projectsai.saibackend.domain.Member;
import projectsai.saibackend.domain.enums.EventEvaluation;
import projectsai.saibackend.domain.enums.EventPurpose;
import projectsai.saibackend.domain.enums.RelationStatus;
import projectsai.saibackend.domain.enums.RelationType;
import projectsai.saibackend.repository.EventRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional @Slf4j
class EventServiceTest {

    @PersistenceContext EntityManager em;
    @Autowired EventRepository eventRepository;

    private Member owner;
    private Friend friend1, friend2, friend3;
    private List<Friend> friendList;

    @BeforeEach
    void createMemberFriend() {
        owner = new Member("라파파", "rapapa@gmail.com", "aasdf", LocalDate.now());
        friend1 = new Friend("친구1", RelationType.FRIEND, RelationStatus.NORMAL, 50, null, null, null);
        friend2 = new Friend("친구2", RelationType.FRIEND, RelationStatus.NORMAL, 50, null, null, null);
        friend3 = new Friend("친구3", RelationType.FRIEND, RelationStatus.NORMAL, 50, null, null, null);

        owner.addFriend(friend1);
        owner.addFriend(friend2);
        owner.addFriend(friend3);
        em.persist(owner);

        friendList = new ArrayList<>();
        friendList.add(friend1);
        friendList.add(friend2);
        friendList.add(friend3);
    }

    @Test
    void addEvent() {
        // given
        Event event = new Event(LocalDate.now(), EventPurpose.CHILL, "새벽 코딩", EventEvaluation.GREAT, friendList);

        // when
        Long savedEventId = eventRepository.save(owner, event);

        // then
        Assertions.assertEquals(savedEventId, event.getId());
    }

    @Test
    void findById() {
        // given
        Event event = new Event(LocalDate.now(), EventPurpose.CHILL, "새벽 코딩", EventEvaluation.GREAT, friendList);
        Long savedEventId = eventRepository.save(owner, event);

        // when
        Event findEvent = eventRepository.findById(owner, savedEventId);

        // then
        Assertions.assertEquals(savedEventId, findEvent.getId());
    }

    @Test
    void findAll() {
        // given
        Event event1 = new Event(LocalDate.now(), EventPurpose.CHILL, "새벽 코딩", EventEvaluation.GREAT, friendList);
        Event event2 = new Event(LocalDate.now(), EventPurpose.CHILL, "저녁 코딩", EventEvaluation.GREAT, friendList);
        Event event3 = new Event(LocalDate.now(), EventPurpose.CHILL, "아침 코딩", EventEvaluation.GREAT, friendList);
        Long savedEventId1 = eventRepository.save(owner, event1);
        Long savedEventId2 = eventRepository.save(owner, event2);
        Long savedEventId3 = eventRepository.save(owner, event3);

        // when
        List<Event> findEventList = eventRepository.findAll(owner);

        // then
        for(Event event : findEventList) {
            org.assertj.core.api.Assertions.assertThat(event).isIn(event1, event2, event3);
        }
    }

    @Test @Rollback(value = false)
    void findByParticipants() {
        // given
        List<Friend> friendList1 = friendList.subList(0, 2);
        List<Friend> friendList2 = friendList.subList(1, 3);
        Event event1 = new Event(LocalDate.now(), EventPurpose.CHILL, "새벽 코딩", EventEvaluation.GREAT, friendList);
        Event event2 = new Event(LocalDate.now(), EventPurpose.CHILL, "저녁 코딩", EventEvaluation.GREAT, friendList1);
        Event event3 = new Event(LocalDate.now(), EventPurpose.CHILL, "아침 코딩", EventEvaluation.GREAT, friendList2);
        eventRepository.save(owner, event1);
        eventRepository.save(owner, event2);
        eventRepository.save(owner, event3);

        // when
        List<Event> findEventList = eventRepository.findByParticipants(owner, friendList);

        // then
        for(Event event : findEventList) {
            org.assertj.core.api.Assertions.assertThat(event.getName()).isIn(event1.getName(), event2.getName(), event3.getName());
        }
    }

    @Test
    void findByName() {
        // given
        // when
        // then
    }

    @Test
    void findByDate() {
        // given
        // when
        // then
    }

    @Test
    void findByPurpose() {
        // given
        // when
        // then
    }

    @Test
    void findByEvaluation() {
        // given
        // when
        // then
    }
}