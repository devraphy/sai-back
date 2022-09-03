package projectsai.saibackend.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import projectsai.saibackend.domain.Event;
import projectsai.saibackend.domain.Friend;
import projectsai.saibackend.domain.Member;
import projectsai.saibackend.domain.enums.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
@Transactional
class EventServiceTest {

    @PersistenceContext
    EntityManager em;
    @Autowired
    EventService eventService;

    private Member owner;
    private Event event1, event2, event3, event4;
    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    void createMemberFriendEvent() {
        owner = new Member("라파파", "rapapa@gmail.com", passwordEncoder.encode("abcde"), Boolean.TRUE, "ROLE_USER");
        Friend friend1 = new Friend(owner, "친구1", RelationType.FRIEND, RelationStatus.NORMAL, 50, null);
        Friend friend2 = new Friend(owner, "친구2", RelationType.FRIEND, RelationStatus.NORMAL, 50, null);
        Friend friend3 = new Friend(owner, "친구3", RelationType.FRIEND, RelationStatus.NORMAL, 50, null);

        em.persist(owner);
        em.persist(friend1);
        em.persist(friend2);
        em.persist(friend3);

        List<Friend> friendList = new ArrayList<>();
        friendList.add(friend1);
        friendList.add(friend2);
        friendList.add(friend3);

        event1 = new Event(owner, LocalDate.now(), EventPurpose.CHILL, "오늘 코딩", EventEvaluation.POSITIVE);
        event2 = new Event(owner, LocalDate.of(2022, 07, 10), EventPurpose.CHILL, "알고리즘 스터디", EventEvaluation.POSITIVE);
        event3 = new Event(owner, LocalDate.of(2022, 6, 6), EventPurpose.WORK, "6월 결산 회의", EventEvaluation.NORMAL);
        event4 = new Event(owner, LocalDate.of(2022, 7, 7), EventPurpose.WORK, "7월 결산 회의", EventEvaluation.NORMAL);
    }

    @Test
    @DisplayName("Event - 이벤트 저장")
    void addEvent() throws Exception {
        // given

        // when
        Long savedEventId = eventService.addEvent(event1);

        // then
        Assertions.assertEquals(savedEventId, event1.getEventId());
    }

    @Test
    @DisplayName("Event - ID로 이벤트 검색")
    void findById() throws Exception {
        // given
        Long savedEventId = eventService.addEvent(event1);

        // when
        Event findEvent = eventService.findById(savedEventId);

        // then
        Assertions.assertEquals(savedEventId, findEvent.getEventId());
    }

    @Test
    @DisplayName("Event - 모든 이벤트 검색")
    void findAll() throws Exception {
        // given
        Long savedEventId1 = eventService.addEvent(event1);
        Long savedEventId2 = eventService.addEvent(event2);
        Long savedEventId3 = eventService.addEvent(event3);
        List<Event> events = Arrays.asList(event1, event2, event3);

        // when
        List<Event> findEventList = eventService.findAll(owner);

        // then
        if (findEventList.isEmpty()) {
            Assertions.fail("findAll() => findEventList is empty.");
        }

        Assertions.assertEquals(events, findEventList);
    }

    @Test
    @DisplayName("Event - 이름으로 이벤트 검색 ")
    void findByName() throws Exception {
        // given
        eventService.addEvent(event1);

        // when
        List<Event> findEventList = eventService.findByName(owner, "오늘 코딩");

        // then
        if (findEventList.isEmpty()) {
            Assertions.fail("findByName() => findEventList is empty.");
        }

        for (Event event : findEventList) {
            Assertions.assertEquals(event1.getName(), event.getName());
        }
    }

    @Test
    @DisplayName("Event - 날짜로 이벤트 검색")
    void findByDate() throws Exception {
        // given
        eventService.addEvent(event1);
        eventService.addEvent(event2);
        eventService.addEvent(event3);
        eventService.addEvent(event4);

        // when
        List<Event> findEventList = eventService.findByDate(owner, LocalDate.now());

        // then
        if (findEventList.isEmpty()) {
            Assertions.fail("findByDate() => findEventList is empty.");
        }

        for (Event event : findEventList) {
            Assertions.assertEquals(event1.getName(), event.getName());
        }
    }

    @Test
    @DisplayName("Event - 목적으로 이벤트 검색")
    void findByPurpose() throws Exception {
        // given
        eventService.addEvent(event1);
        eventService.addEvent(event2);
        eventService.addEvent(event3);
        eventService.addEvent(event4);

        // when
        List<Event> chillEvents = eventService.findByPurpose(owner, EventPurpose.CHILL);
        List<Event> businessEvents = eventService.findByPurpose(owner, EventPurpose.WORK);

        // then
        if (chillEvents.isEmpty()) {
            Assertions.fail("findByPurpose() => chillEvents is empty.");
        }

        if (businessEvents.isEmpty()) {
            Assertions.fail("findByPurpose() => businessEvents is empty.");
        }

        for (Event event : chillEvents) {
            Assertions.assertEquals(EventPurpose.CHILL, event.getPurpose());
        }

        for (Event event : businessEvents) {
            Assertions.assertEquals(EventPurpose.WORK, event.getPurpose());
        }
    }

    @Test
    @DisplayName("Event - 평가로 이벤트 검색")
    void findByEvaluation() throws Exception {
        // given
        eventService.addEvent(event1);
        eventService.addEvent(event2);
        eventService.addEvent(event3);
        eventService.addEvent(event4);

        // when
        List<Event> normalEvents = eventService.findByEvaluation(owner, EventEvaluation.NORMAL);
        List<Event> positiveEvents = eventService.findByEvaluation(owner, EventEvaluation.POSITIVE);

        // then

        if (normalEvents.isEmpty()) {
            Assertions.fail("findByEvaluation() => normalEvents is empty.");
        }

        if (positiveEvents.isEmpty()) {
            Assertions.fail("findByEvaluation() => positiveEvents is empty.");
        }

        for (Event event : normalEvents) {
            Assertions.assertEquals(EventEvaluation.NORMAL, event.getEvaluation());
        }

        for (Event event : positiveEvents) {
            Assertions.assertEquals(EventEvaluation.POSITIVE, event.getEvaluation());
        }
    }

    @Test
    @DisplayName("Event - 이벤트 수정")
    public void updateEvent() throws Exception {
        //given
        Long savedEventId = eventService.addEvent(event1);

        //when
        eventService.updateEvent(savedEventId,
                "이벤트업데이트", LocalDate.of(2022, 8, 8),
                EventPurpose.CHILL, EventEvaluation.NORMAL);

        //then
        Assertions.assertEquals("이벤트업데이트", event1.getName());
    }

    @Test
    @DisplayName("Event - 이벤트 삭제")
    public void deleteEvent() throws Exception {
        //given
        Long savedEventId = eventService.addEvent(event1);

        //when
        boolean result = eventService.deleteEvent(event1);

        //then
        Assertions.assertEquals(null, eventService.findById(savedEventId));
    }
}