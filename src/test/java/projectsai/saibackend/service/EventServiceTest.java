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
    private Event event1, event2, event3, event4;
    private List<Friend> friendList;

    @BeforeEach
    void createMemberFriendEvent() {
        owner = new Member("라파파", "rapapa@gmail.com", "aasdf", LocalDate.now(), Boolean.TRUE);
        friend1 = new Friend("친구1", RelationType.FRIEND, RelationStatus.NORMAL, 50, null, null);
        friend2 = new Friend("친구2", RelationType.FRIEND, RelationStatus.NORMAL, 50, null, null);
        friend3 = new Friend("친구3", RelationType.FRIEND, RelationStatus.NORMAL, 50, null, null);

        owner.addFriend(friend1);
        owner.addFriend(friend2);
        owner.addFriend(friend3);
        em.persist(owner);

        friendList = new ArrayList<>();
        friendList.add(friend1);
        friendList.add(friend2);
        friendList.add(friend3);

        event1 = new Event(LocalDate.now(), EventPurpose.CHILL, "오늘 코딩", EventEvaluation.POSITIVE, friendList);
        event2 = new Event(LocalDate.of(2022, 07, 10), EventPurpose.CHILL, "알고리즘 스터디", EventEvaluation.POSITIVE, friendList);
        event3 = new Event(LocalDate.of(2022, 6, 6), EventPurpose.WORK, "6월 결산 회의", EventEvaluation.NORMAL, friendList);
        event4 = new Event(LocalDate.of(2022, 7, 7), EventPurpose.WORK, "7월 결산 회의", EventEvaluation.NORMAL, friendList);
    }

    @Test
    void addEvent() {
        // given

        // when
        Long savedEventId = eventRepository.save(owner, event1);

        // then
        Assertions.assertEquals(savedEventId, event1.getId());
    }

    @Test
    void findById() {
        // given
        Long savedEventId = eventRepository.save(owner, event1);

        // when
        Event findEvent = eventRepository.findById(owner.getId(), savedEventId);

        // then
        Assertions.assertEquals(savedEventId, findEvent.getId());
    }

    @Test
    void findAll() {
        // given
        Long savedEventId1 = eventRepository.save(owner, event1);
        Long savedEventId2 = eventRepository.save(owner, event2);
        Long savedEventId3 = eventRepository.save(owner, event3);

        // when
        List<Event> findEventList = eventRepository.findAll(owner.getId());

        // then
        if(findEventList.isEmpty()) {
            Assertions.fail("findAll() => findEventList is empty.");
        }

        for(Event event : findEventList) {
            org.assertj.core.api.Assertions.assertThat(event).isIn(event1, event2, event3);
        }
    }

    @Test
    void findByParticipants() {
        // given
        List<Friend> friendList1 = friendList.subList(0, 2);
        List<Friend> friendList2 = friendList.subList(1, 3);

        event1 = new Event(LocalDate.now(), EventPurpose.CHILL, "새벽 코딩", EventEvaluation.GREAT, friendList);
        event2 = new Event(LocalDate.now(), EventPurpose.CHILL, "저녁 코딩", EventEvaluation.GREAT, friendList1);
        event3 = new Event(LocalDate.now(), EventPurpose.CHILL, "아침 코딩", EventEvaluation.GREAT, friendList2);

        eventRepository.save(owner, event1);
        eventRepository.save(owner, event2);
        eventRepository.save(owner, event3);

        // when
        List<Event> findEventList = eventRepository.findByParticipants(owner.getId(), friendList);

        // then
        if(findEventList.isEmpty()) {
            Assertions.fail("findByParticipants() => findEventList is empty.");
        }

        for(Event event : findEventList) {
            org.assertj.core.api.Assertions.assertThat(event.getName()).isIn("새벽 코딩", "저녁 코딩", "아침 코딩");
        }
    }

    @Test
    void findByName() {
        // given
        eventRepository.save(owner, event1);

        // when
        List<Event> findEventList = eventRepository.findByEventName(owner.getId(), "오늘 코딩");

        // then
        if(findEventList.isEmpty()) {
            Assertions.fail("findByName() => findEventList is empty.");
        }

        for(Event event : findEventList) {
            Assertions.assertEquals(event.getName(), event1.getName());
        }
    }

    @Test
    void findByDate() {
        // given
        eventRepository.save(owner, event1);
        eventRepository.save(owner, event2);
        eventRepository.save(owner, event3);
        eventRepository.save(owner, event4);

        // when
        List<Event> findEventList = eventRepository.findByDate(owner.getId(), LocalDate.now());

        // then
        if(findEventList.isEmpty()) {
            Assertions.fail("findByDate() => findEventList is empty.");
        }

        for(Event event : findEventList) {
            Assertions.assertEquals(event.getName(), event1.getName());
        }
    }

    @Test
    void findByPurpose() {
        // given
        eventRepository.save(owner, event1);
        eventRepository.save(owner, event2);
        eventRepository.save(owner, event3);
        eventRepository.save(owner, event4);

        // when
        List<Event> chillEvents = eventRepository.findByPurpose(owner.getId(), EventPurpose.CHILL);
        List<Event> businessEvents = eventRepository.findByPurpose(owner.getId(), EventPurpose.WORK);

        // then
        if(chillEvents.isEmpty()) {
            Assertions.fail("findByPurpose() => chillEvents is empty.");
        }

        if(businessEvents.isEmpty()) {
            Assertions.fail("findByPurpose() => businessEvents is empty.");
        }

        for(Event event : chillEvents) {
            log.info("chill event name => " + event.getName());
            org.assertj.core.api.Assertions.assertThat(event.getPurpose()).isIn(EventPurpose.CHILL);
        }

        for(Event event : businessEvents) {
            log.info("business event name => " + event.getName());
            org.assertj.core.api.Assertions.assertThat(event.getPurpose()).isIn(EventPurpose.WORK);
        }
    }

    @Test
    void findByEvaluation() {
        // given
        eventRepository.save(owner, event1);
        eventRepository.save(owner, event2);
        eventRepository.save(owner, event3);
        eventRepository.save(owner, event4);

        // when
        List<Event> normalEvents = eventRepository.findByEvaluation(owner.getId(), EventEvaluation.NORMAL);
        List<Event> positiveEvents = eventRepository.findByEvaluation(owner.getId(), EventEvaluation.POSITIVE);

        // then

        if(normalEvents.isEmpty()) {
            Assertions.fail("findByEvaluation() => normalEvents is empty.");
        }

        if(positiveEvents.isEmpty()) {
            Assertions.fail("findByEvaluation() => positiveEvents is empty.");
        }

        for(Event event : normalEvents) {
            log.info("normal event name => " + event.getName());
            org.assertj.core.api.Assertions.assertThat(event.getEvaluation()).isIn(EventEvaluation.NORMAL);
        }

        for(Event event : positiveEvents) {
            log.info("positive event name => " + event.getName());
            org.assertj.core.api.Assertions.assertThat(event.getEvaluation()).isIn(EventEvaluation.POSITIVE);
        }
    }
}