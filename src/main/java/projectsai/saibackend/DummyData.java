package projectsai.saibackend;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import projectsai.saibackend.domain.*;
import projectsai.saibackend.domain.enums.*;

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

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService {

        private final EntityManager em;
        private final PasswordEncoder passwordEncoder;

        public void dbInit1() {
            User user1 = new User("Raphael Lee" ,"raphaellee1014@gmali.com", passwordEncoder.encode("abcabc"), 1);
            em.persist(user1);

            Friend friend1 = new Friend(user1, "아인", RelationType.FRIEND, RelationStatus.NORMAL, 50,  null, null);
            Friend friend2 = new Friend(user1, "쯔바이", RelationType.FRIEND, RelationStatus.NORMAL, 50, null, null);
            Friend friend3 = new Friend(user1, "드라이", RelationType.FRIEND, RelationStatus.POSITIVE, 70, null, null);
            Friend friend4 = new Friend(user1, "피어", RelationType.FRIEND, RelationStatus.NEGATIVE, 30, null, null);
            em.persist(friend1);
            em.persist(friend2);
            em.persist(friend3);
            em.persist(friend4);

            Event event1 = new Event(user1, LocalDate.now(), EventPurpose.CHILL, "첫번째놀자", EventEvaluation.GREAT);
            Event event2 = new Event(user1, LocalDate.now(), EventPurpose.CHILL, "두번째놀자", EventEvaluation.GREAT);
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
            User user2 = new User("David Lee" ,"devraphy@gmali.com", passwordEncoder.encode("123123"), 1);
            em.persist(user2);

            Friend friend1 = new Friend(user2,"지벤", RelationType.BUSINESS, RelationStatus.NORMAL, 50, null, null);
            Friend friend2 = new Friend(user2,"아크트", RelationType.BUSINESS, RelationStatus.NORMAL, 50, null, null);
            Friend friend3 = new Friend(user2,"노인", RelationType.BUSINESS, RelationStatus.POSITIVE, 70, null, null);
            Friend friend4 = new Friend(user2, "첸", RelationType.BUSINESS, RelationStatus.NEGATIVE, 30, null, null);
            em.persist(friend1);
            em.persist(friend2);
            em.persist(friend3);
            em.persist(friend4);


            Event event1 = new Event(user2, LocalDate.now(), EventPurpose.WORK, "첫번째회의", EventEvaluation.GREAT);
            Event event2 = new Event(user2, LocalDate.now(), EventPurpose.WORK, "두번째회의", EventEvaluation.GREAT);
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

            User user3 = new User("test1" ,"test@gmail.com",passwordEncoder.encode("abcabc") , 1);
            em.persist(user3);

            User user4 = new User("test2" ,"resign@gmail.com", passwordEncoder.encode("123123"), 0);
            em.persist(user4);
        }
    }
}
