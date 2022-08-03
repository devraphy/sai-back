package projectsai.saibackend;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import projectsai.saibackend.domain.Event;
import projectsai.saibackend.domain.Friend;
import projectsai.saibackend.domain.Member;
import projectsai.saibackend.domain.enums.EventEvaluation;
import projectsai.saibackend.domain.enums.EventPurpose;
import projectsai.saibackend.domain.enums.RelationStatus;
import projectsai.saibackend.domain.enums.RelationType;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
            Member member1 = new Member("Raphael Lee" ,"raphaellee1014@gmali.com", "abcabc", LocalDate.now(), Boolean.TRUE);
            em.persist(member1);

            Friend friend1 = new Friend("아인", RelationType.FRIEND, RelationStatus.NORMAL, 50, null, null, null);
            Friend friend2 = new Friend("쯔바이", RelationType.FRIEND, RelationStatus.NORMAL, 50, null, null, null);
            Friend friend3 = new Friend("드라이", RelationType.FRIEND, RelationStatus.POSITIVE, 60, null, null, null);
            Friend friend4 = new Friend("피어", RelationType.FRIEND, RelationStatus.NEGATIVE, 40, null, null, null);

            member1.addFriend(friend1);
            member1.addFriend(friend2);
            member1.addFriend(friend3);
            member1.addFriend(friend4);

            List<Friend> friendList1 = new ArrayList<>();
            friendList1.add(friend1);
            friendList1.add(friend2);
            friendList1.add(friend3);
            friendList1.add(friend4);

            Event event1 = new Event(LocalDate.now(), EventPurpose.CHILL, "첫번째놀자", EventEvaluation.GREAT, friendList1);
            member1.addEvent(event1);

            Event event2 = new Event(LocalDate.now(), EventPurpose.CHILL, "두번째놀자", EventEvaluation.GREAT, friendList1.subList(2, 4));
            member1.addEvent(event2);
        }

        public void dbInit2() {
            Member member2 = new Member("David Lee" ,"devRaphy@gmali.com", "123123", LocalDate.of(2022, 8, 8), Boolean.TRUE);
            em.persist(member2);

            Friend friend7 = new Friend("지벤", RelationType.BUSINESS, RelationStatus.NORMAL, 50, null, null, null);
            Friend friend8 = new Friend("아크트", RelationType.BUSINESS, RelationStatus.NORMAL, 50, null, null, null);
            Friend friend9 = new Friend("노인", RelationType.BUSINESS, RelationStatus.POSITIVE, 60, null, null, null);
            Friend friend10 = new Friend("첸", RelationType.BUSINESS, RelationStatus.NEGATIVE, 40, null, null, null);

            member2.addFriend(friend7);
            member2.addFriend(friend8);
            member2.addFriend(friend9);
            member2.addFriend(friend10);

            List<Friend> friendList2 = new ArrayList<>();
            friendList2.add(friend7);
            friendList2.add(friend8);
            friendList2.add(friend9);
            friendList2.add(friend10);

            Event event1 = new Event(LocalDate.now(), EventPurpose.BUSINESS, "첫번째회의", EventEvaluation.GREAT, friendList2);
            member2.addEvent(event1);

            Event event2 = new Event(LocalDate.now(), EventPurpose.BUSINESS, "두번째회의", EventEvaluation.GREAT, friendList2.subList(2, 4));
            member2.addEvent(event2);
        }
    }
}
