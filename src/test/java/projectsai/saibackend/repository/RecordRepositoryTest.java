package projectsai.saibackend.repository;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
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
import projectsai.saibackend.domain.Record;
import projectsai.saibackend.domain.enums.EventEvaluation;
import projectsai.saibackend.domain.enums.EventPurpose;
import projectsai.saibackend.domain.enums.RelationStatus;
import projectsai.saibackend.domain.enums.RelationType;

import javax.management.relation.Relation;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Slf4j
class RecordRepositoryTest {

    @PersistenceContext
    EntityManager em;
    @Autowired
    RecordRepository recordRepository;
    BCryptPasswordEncoder passwordEncoder;

    private Member member;
    private Event event;
    private Friend friend1, friend2;
    private Record record1, record2;

    @BeforeEach
    void createEventFriendRecord() {
        member = new Member("테스트", "test@gmail.com",
                passwordEncoder.encode("test"), Boolean.TRUE, "ROLE_USER");
        em.persist(member);

        friend1 = new Friend(member, "친구1", RelationType.FRIEND, RelationStatus.NORMAL, 50, null);
        friend2 = new Friend(member, "친구2", RelationType.FRIEND, RelationStatus.NORMAL, 50, null);
        em.persist(friend1);
        em.persist(friend2);

        event = new Event(member, LocalDate.now(), EventPurpose.CHILL, "test", EventEvaluation.GREAT);
        em.persist(event);

        record1 = new Record(event, friend1);
        record2 = new Record(event, friend2);
    }

    @Test
    @DisplayName("Record - 기록 저장")
    void addRecord() throws Exception {
        // given

        // when
        Long savedRecordId = recordRepository.addRecord(record1);

        // then
        Assertions.assertThat(savedRecordId).isEqualTo(record1.getRecordId());
    }

    @Test
    @DisplayName("Record - Id로 기록 검색")
    public void findById() throws Exception {
        //given
        Long savedRecordId = recordRepository.addRecord(record1);

        //when
        Record findRecord = recordRepository.findById(savedRecordId);

        //then
        Assertions.assertThat(findRecord).isEqualTo(record1);

    }

    @Test
    @DisplayName("Record - 이벤트로 모든 기록 검색")
    void findAll() throws Exception {
        // given
        recordRepository.addRecord(record1);
        recordRepository.addRecord(record2);

        // when
        List<Record> allRecord = recordRepository.findAll(event);

        // then
        for (Record record : allRecord) {
            Assertions.assertThat(record.getEvent()).isEqualTo(event);
        }
    }

    @Test
    @DisplayName("Record - 단일 참가자로 기록 검색")
    void findByParticipant() throws Exception {
        // given

        // when
        List<Record> allRecord = recordRepository.findByParticipant(friend1);

        // then
        for (Record record : allRecord) {
            Assertions.assertThat(record.getFriend()).isEqualTo(friend1);
        }
    }

    @Test
    @DisplayName("Record - 특정 기록 검색")
    void findOne() throws Exception {
        // given
        recordRepository.addRecord(record1);

        // when
        Record oneRecord = recordRepository.findOne(event, friend1);

        // then
        Assertions.assertThat(oneRecord.getRecordId()).isEqualTo(record1.getRecordId());
    }

    @Test
    @DisplayName("Record - 이벤트로 모든 기록 삭제")
    public void deleteAllRecord() throws Exception {
        //given

        //when
        recordRepository.deleteAllRecords(event);

        //then
        Assertions.assertThat(recordRepository.findAll(event).size()).isEqualTo(0);

    }

    @Test
    @DisplayName("Record - 특정 기록 삭제")
    public void deleteRecord() throws Exception {
        //given
        Long savedRecordId = recordRepository.addRecord(record1);

        //when
        recordRepository.deleteRecord(record1);

        //then
        Assertions.assertThat(recordRepository.findById(savedRecordId)).isEqualTo(null);

    }
}