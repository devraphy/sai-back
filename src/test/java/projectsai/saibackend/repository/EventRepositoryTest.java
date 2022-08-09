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
    private List<Friend> friendList, businessList;

    @BeforeEach
    public void createMemberAndFriend() {
        owner = new Member("이근형","abc@gmail.com", "abcdefg", LocalDate.now(), Boolean.TRUE);
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
        friendList = findFriends();
        businessList = findBusiness();

        Event friendEvent1 = new Event(owner, LocalDate.now(), EventPurpose.CHILL, "친구 모임", EventEvaluation.NORMAL, friend1);
        Event friendEvent2 = new Event(owner, LocalDate.now(), EventPurpose.CHILL, "친구 모임", EventEvaluation.NORMAL, friend2);
        eventRepository.save(friendEvent1);
        eventRepository.save(friendEvent2);

        Event businessEvent1 = new Event(owner, LocalDate.now(), EventPurpose.WORK, "비지니스 모임", EventEvaluation.POSITIVE, business1);
        Event businessEvent2 = new Event(owner, LocalDate.now(), EventPurpose.WORK, "비지니스 모임", EventEvaluation.POSITIVE, business2);
        eventRepository.save(businessEvent1);
        eventRepository.save(businessEvent2);
    }

    public List<Friend> findFriends() {
        return em.createQuery("select f from Friend f " +
                        "where f.owner.id =: ownerId " +
                        "and f.type = :relationType", Friend.class)
                .setParameter("ownerId", this.owner.getId())
                .setParameter("relationType", RelationType.FRIEND)
                .getResultList();
    }

    public List<Friend> findBusiness() {
        return em.createQuery("select f from Friend f " +
                        "where f.owner.id =: ownerId " +
                        "and f.type = :relationType", Friend.class)
                .setParameter("ownerId", this.owner.getId())
                .setParameter("relationType", RelationType.BUSINESS)
                .getResultList();
    }

    @Test @DisplayName("Event - 전체 검색")
    void findAll() {
        //given

        //when
        List<Event> allEvent = eventRepository.findAll(owner.getId());

        //then
        for(Event event : allEvent) {
            Assertions.assertThat(event.getFriend()).isIn(friend1, friend2, business1, business2);
        }
    }

    @Test @DisplayName("Event - 참가자로 검색")
    void findByFriend() {
        //given

        //when
        List<Event> friendEventList = eventRepository.findByParticipants(owner.getId(), friendList);
        List<Event> businessEventList = eventRepository.findByParticipants(owner.getId(), businessList);

        //then
        for(Event event : friendEventList) {
            Assertions.assertThat(event.getFriend()).isIn(friend1, friend2);
        }

        for(Event event : businessEventList) {
            Assertions.assertThat(event.getFriend()).isIn(business1, business2);
        }
    }

    @Test @DisplayName("Event - 이름으로 검색")
    void findByEventName() {
        //given

        //when
        List<Event> eventList = eventRepository.findByEventName(owner.getId(), "친구 모임");

        //then
        for(Event event: eventList) {
            Assertions.assertThat(event.getName()).isEqualTo("친구 모임");
        }
    }

    @Test @DisplayName("Event - 날짜로 검색")
    void findByDate() {
        //given

        //when
        List<Event> eventList = eventRepository.findByDate(owner.getId(), LocalDate.now());

        //then
        for(Event event : eventList) {
            Assertions.assertThat(event.getDate()).isEqualTo(LocalDate.now());
        }
    }

    @Test @DisplayName("Event - 목적으로 검색")
    void findByPurpose() {
        //given

        //when
        List<Event> chillEvents = eventRepository.findByPurpose(owner.getId(), EventPurpose.CHILL);
        List<Event> businessEvents = eventRepository.findByPurpose(owner.getId(), EventPurpose.WORK);

        //then
        for(Event event : chillEvents) {
            Assertions.assertThat(event.getPurpose()).isEqualTo(EventPurpose.CHILL);
        }

        for(Event event : businessEvents) {
            Assertions.assertThat(event.getPurpose()).isEqualTo(EventPurpose.WORK);
        }
    }

    @Test @DisplayName("Event - 평가로 검색")
    void findByEvaluation() {
        //given

        //when
        List<Event> normalEvents = eventRepository.findByEvaluation(owner.getId(), EventEvaluation.NORMAL);
        List<Event> positiveEvents = eventRepository.findByEvaluation(owner.getId(), EventEvaluation.POSITIVE);

        //then
        for(Event event : normalEvents) {
            Assertions.assertThat(event.getEvaluation()).isEqualTo(EventEvaluation.NORMAL);
        }

        for(Event event : positiveEvents) {
            Assertions.assertThat(event.getEvaluation()).isEqualTo(EventEvaluation.POSITIVE);
        }
    }
}