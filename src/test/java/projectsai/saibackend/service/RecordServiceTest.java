package projectsai.saibackend.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
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
@Transactional @Slf4j
class RecordServiceTest {

    @PersistenceContext EntityManager em;
    @Autowired RecordService recordService;

    private Event event;
    private Friend friend;
    private Record record;

    @BeforeEach
    void createEventFriendRecord() {
        event = em.find(Event.class, Long.valueOf(6));
        friend = em.find(Friend.class, Long.valueOf(14));
        record = new Record(event, friend);
    }

    @Test @DisplayName("Record - 이벤트 기록 저장")
    void addRecord() {
        // given

        // when
        recordService.addRecord(record);

        // then
        Assertions.assertEquals(record, em.find(Record.class, record.getRecordId()));

    }

    @Test @DisplayName("Record - 특정 이벤트의 모든 기록 검색")
    void findAllParticipants() {
        // given

        // when
        List<Record> recordList = recordService.findAll(event);

        // then
        for (Record one : recordList) {
            Assertions.assertEquals(event, one.getEvent());
        }
    }

    @Test @DisplayName("Record - 특정 참가자의 모든 이벤트 기록 검색")
    void findByParticipant() {
        // given

        // when
        List<Record> recordList = recordService.findByParticipant(friend);

        // then
        for (Record one : recordList) {
            Assertions.assertEquals(friend, one.getFriend());
        }
    }

    @Test @DisplayName("Record - 특정한 이벤트 기록 검색")
    void findOne() {
        // given
        recordService.addRecord(record);

        // when
        Record one = recordService.findOne(event, friend);

        // then
        Assertions.assertEquals(record, one);
    }
}