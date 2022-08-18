package projectsai.saibackend.repository;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import projectsai.saibackend.domain.Event;
import projectsai.saibackend.domain.Friend;
import projectsai.saibackend.domain.Member;
import projectsai.saibackend.domain.enums.EventEvaluation;
import projectsai.saibackend.domain.enums.EventPurpose;
import projectsai.saibackend.domain.enums.RelationStatus;
import projectsai.saibackend.domain.enums.RelationType;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.time.LocalDate;
import java.util.List;


@SpringBootTest @Slf4j @Transactional
class EventRepositoryTest {

    @Autowired EventRepository eventRepository;
    @PersistenceContext EntityManager em;

    private Member owner;
    private Friend friend1, friend2, business1, business2;

    @BeforeEach
    public void createMemberAndFriend() {
        owner = new Member("이근형","abc@gmail.com", "abcdefg", 1);
        friend1 = new Friend(owner, "친구1", RelationType.FRIEND, RelationStatus.NORMAL, 50, null, null);
        friend2 = new Friend(owner, "친구2", RelationType.FRIEND, RelationStatus.POSITIVE, 80, null, null);
        business1 = new Friend(owner, "동료1", RelationType.BUSINESS, RelationStatus.NORMAL, 50, null, null);
        business2 = new Friend(owner, "동료2", RelationType.BUSINESS, RelationStatus.NORMAL, 50, null, null);

        em.persist(owner);
        em.persist(friend1);
        em.persist(friend2);
        em.persist(business1);
        em.persist(business2);
    }

    @BeforeEach
    public void createEvent() {
        Event friendEvent1 = new Event(owner, LocalDate.now(), EventPurpose.CHILL, "친구 모임", EventEvaluation.NORMAL);
        Event friendEvent2 = new Event(owner, LocalDate.now(), EventPurpose.CHILL, "친구 모임", EventEvaluation.NORMAL);
        Event businessEvent1 = new Event(owner, LocalDate.now(), EventPurpose.WORK, "비지니스 모임", EventEvaluation.POSITIVE);
        Event businessEvent2 = new Event(owner, LocalDate.now(), EventPurpose.WORK, "비지니스 모임", EventEvaluation.POSITIVE);

        em.persist(friendEvent1);
        em.persist(friendEvent2);
        em.persist(businessEvent1);
        em.persist(businessEvent2);
    }

    @Test @DisplayName("Event - 이벤트 저장")
    public void addEvent() throws Exception {
        //given
        Event testEvent = new Event(owner, LocalDate.now(), EventPurpose.CHILL, "테스트 모임", EventEvaluation.NORMAL);

        //when
        eventRepository.addEvent(testEvent);

        //then
        Assertions.assertThat(testEvent.getName()).isEqualTo("테스트 모임");
    }

    @Test @DisplayName("Event - ID로 이벤트 검색")
    public void findById() throws Exception {
        //given
        Event testEvent = new Event(owner, LocalDate.now(), EventPurpose.CHILL, "테스트 모임", EventEvaluation.NORMAL);
        Long savedEventId = eventRepository.addEvent(testEvent);

        //when
        Event findEvent = eventRepository.findById(savedEventId);

        // then
        Assertions.assertThat(findEvent.getEventId()).isEqualTo(savedEventId);
    }

    @Test @DisplayName("Event - 전체 검색")
    void findAll() throws Exception {
        //given

        //when
        List<Event> allEvent = eventRepository.findAll(owner);

        //then
        for(Event event : allEvent) {
            Assertions.assertThat(event.getName()).isIn("친구 모임", "비지니스 모임");
        }
    }

    @Test @DisplayName("Event - 이름으로 검색")
    void findByEventName() throws Exception {
        //given

        //when
        List<Event> eventList = eventRepository.findByEventName(owner, "친구 모임");

        //then
        for(Event event: eventList) {
            Assertions.assertThat(event.getName()).isEqualTo("친구 모임");
        }
    }

    @Test @DisplayName("Event - 날짜로 검색")
    void findByDate() throws Exception {
        //given

        //when
        List<Event> eventList = eventRepository.findByDate(owner, LocalDate.now());

        //then
        for(Event event : eventList) {
            Assertions.assertThat(event.getDate()).isEqualTo(LocalDate.now());
        }
    }

    @Test @DisplayName("Event - 목적으로 검색")
    void findByPurpose() throws Exception {
        //given

        //when
        List<Event> chillEvents = eventRepository.findByPurpose(owner, EventPurpose.CHILL);
        List<Event> businessEvents = eventRepository.findByPurpose(owner, EventPurpose.WORK);

        //then
        for(Event event : chillEvents) {
            Assertions.assertThat(event.getPurpose()).isEqualTo(EventPurpose.CHILL);
        }

        for(Event event : businessEvents) {
            Assertions.assertThat(event.getPurpose()).isEqualTo(EventPurpose.WORK);
        }
    }

    @Test @DisplayName("Event - 평가로 검색")
    void findByEvaluation() throws Exception {
        //given

        //when
        List<Event> normalEvents = eventRepository.findByEvaluation(owner, EventEvaluation.NORMAL);
        List<Event> positiveEvents = eventRepository.findByEvaluation(owner, EventEvaluation.POSITIVE);

        //then
        for(Event event : normalEvents) {
            Assertions.assertThat(event.getEvaluation()).isEqualTo(EventEvaluation.NORMAL);
        }

        for(Event event : positiveEvents) {
            Assertions.assertThat(event.getEvaluation()).isEqualTo(EventEvaluation.POSITIVE);
        }
    }

    @Test @DisplayName("Event - 이벤트 삭제")
    public void deleteEvent() throws Exception {
        //given
        Event testEvent = new Event(owner, LocalDate.now(), EventPurpose.CHILL, "테스트 모임", EventEvaluation.NORMAL);
        Long savedEventId = eventRepository.addEvent(testEvent);

        //when
        eventRepository.deleteEvent(testEvent);

        //then
        Assertions.assertThat(eventRepository.findById(savedEventId)).isEqualTo(null);
    }
}