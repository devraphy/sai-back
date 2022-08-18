package projectsai.saibackend;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import projectsai.saibackend.domain.Event;
import projectsai.saibackend.domain.Friend;
import projectsai.saibackend.domain.Member;
import projectsai.saibackend.domain.Record;
import projectsai.saibackend.domain.enums.EventEvaluation;
import projectsai.saibackend.domain.enums.EventPurpose;
import projectsai.saibackend.domain.enums.RelationStatus;
import projectsai.saibackend.domain.enums.RelationType;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class DummyData {

    private final InitService initService;

    @PostConstruct
    public void init() {
        initService.dbInit1();
        initService.dbInit2();
    }

    @Component @Transactional
    @RequiredArgsConstructor
    static class InitService {

        private final EntityManager em;

        public void dbInit1() {
            Member member1 = new Member("Raphael Lee" ,"raphaellee1014@gmali.com", "abcabc", LocalDate.now(), 1);
            em.persist(member1);

            Friend friend1 = new Friend(member1, "아인", RelationType.FRIEND, RelationStatus.NORMAL, 50,  null, null);
            Friend friend2 = new Friend(member1, "쯔바이", RelationType.FRIEND, RelationStatus.NORMAL, 50, null, null);
            Friend friend3 = new Friend(member1, "드라이", RelationType.FRIEND, RelationStatus.POSITIVE, 70, null, null);
            Friend friend4 = new Friend(member1, "피어", RelationType.FRIEND, RelationStatus.NEGATIVE, 30, null, null);
            em.persist(friend1);
            em.persist(friend2);
            em.persist(friend3);
            em.persist(friend4);

            Event event1 = new Event(member1, LocalDate.now(), EventPurpose.CHILL, "첫번째놀자", EventEvaluation.GREAT);
            Event event2 = new Event(member1, LocalDate.now(), EventPurpose.CHILL, "두번째놀자", EventEvaluation.GREAT);
            em.persist(event1);
            em.persist(event2);

            Record record1 = new Record(event1, friend1);
            Record record2 = new Record(event1, friend2);
            Record record3 = new Record(event2, friend3);
            Record record4 = new Record(event2, friend4);
            em.persist(record1);
            em.persist(record2);
            em.persist(record3);
            em.persist(record4);
        }

        public void dbInit2() {
            Member member2 = new Member("David Lee" ,"devRaphy@gmali.com", "123123", LocalDate.of(2022, 8, 8), 1);
            em.persist(member2);

            Friend friend1 = new Friend(member2,"지벤", RelationType.BUSINESS, RelationStatus.NORMAL, 50, null, null);
            Friend friend2 = new Friend(member2,"아크트", RelationType.BUSINESS, RelationStatus.NORMAL, 50, null, null);
            Friend friend3 = new Friend(member2,"노인", RelationType.BUSINESS, RelationStatus.POSITIVE, 70, null, null);
            Friend friend4 = new Friend(member2, "첸", RelationType.BUSINESS, RelationStatus.NEGATIVE, 30, null, null);
            em.persist(friend1);
            em.persist(friend2);
            em.persist(friend3);
            em.persist(friend4);


            Event event1 = new Event(member2, LocalDate.now(), EventPurpose.WORK, "첫번째회의", EventEvaluation.GREAT);
            Event event2 = new Event(member2, LocalDate.now(), EventPurpose.WORK, "두번째회의", EventEvaluation.GREAT);
            em.persist(event1);
            em.persist(event2);

            Record record1 = new Record(event1, friend1);
            Record record2 = new Record(event1, friend2);
            Record record3 = new Record(event2, friend3);
            Record record4 = new Record(event2, friend4);
            em.persist(record1);
            em.persist(record2);
            em.persist(record3);
            em.persist(record4);

            Member member3 = new Member("test1" ,"test@gmail.com", "abcabc", LocalDate.of(2022, 8, 8),1);
            em.persist(member3);

            Member member4 = new Member("test2" ,"resign@gmail.com", "123123", LocalDate.of(2022, 8, 8), 0);
            em.persist(member4);
        }
    }
}
