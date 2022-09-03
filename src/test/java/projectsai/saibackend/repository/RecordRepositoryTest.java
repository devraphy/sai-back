package projectsai.saibackend.repository;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assert;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import projectsai.saibackend.domain.Event;
import projectsai.saibackend.domain.Friend;
import projectsai.saibackend.domain.Record;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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

    private Event event;
    private Friend friend;
    private Record record;

    @BeforeEach
    void createEventFriendRecord() {
        event = em.find(Event.class, 6L);
        friend = em.find(Friend.class, 14L);
        record = new Record(event, friend);
    }

    @Test
    @DisplayName("Record - 기록 저장")
    void addRecord() throws Exception {
        // given

        // when
        Long savedRecordId = recordRepository.addRecord(record);

        // then
        Assertions.assertThat(savedRecordId).isEqualTo(record.getRecordId());
    }

    @Test
    @DisplayName("Record - Id로 기록 검색")
    public void findById() throws Exception {
        //given
        Long savedRecordId = recordRepository.addRecord(record);

        //when
        Record findRecord = recordRepository.findById(savedRecordId);

        //then
        Assertions.assertThat(findRecord).isEqualTo(record);

    }

    @Test
    @DisplayName("Record - 이벤트로 모든 기록 검색")
    void findAll() throws Exception {
        // given

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
        List<Record> allRecord = recordRepository.findByParticipant(friend);

        // then
        for (Record record : allRecord) {
            Assertions.assertThat(record.getFriend()).isEqualTo(friend);
        }
    }

    @Test
    @DisplayName("Record - 특정 기록 검색")
    void findOne() throws Exception {
        // given
        recordRepository.addRecord(record);

        // when
        Record oneRecord = recordRepository.findOne(event, friend);

        // then
        Assertions.assertThat(oneRecord.getRecordId()).isEqualTo(record.getRecordId());
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
        Long savedRecordId = recordRepository.addRecord(record);

        //when
        recordRepository.deleteRecord(record);

        //then
        Assertions.assertThat(recordRepository.findById(savedRecordId)).isEqualTo(null);

    }
}