package projectsai.saibackend.repository;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assert;
import org.assertj.core.api.Assertions;
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

@SpringBootTest @Transactional @Slf4j
class RecordRepositoryTest {

    @PersistenceContext EntityManager em;
    @Autowired RecordRepository recordRepository;

    @Test @DisplayName("Record - 기록 저장")
    void save() {
        // given
        Event event = em.find(Event.class, Long.valueOf(6));
        Friend friend = em.find(Friend.class, Long.valueOf(14));
        Record record = new Record(event, friend);

        // when
        Long savedRecordId = recordRepository.save(record);

        // then
        Assertions.assertThat(savedRecordId).isEqualTo(record.getId());
    }

    @Test @DisplayName("Record - 이벤트로 모든 기록 검색")
    void findAllRecord() {
        // given
        Event event = em.find(Event.class, Long.valueOf(6));

        // when
        List<Record> allRecord = recordRepository.findAllParticipants(event);

        // then
        for (Record record : allRecord) {
            Assertions.assertThat(record.getEvent()).isEqualTo(event);
        }
    }

    @Test @DisplayName("Record - 단일 참가자로 기록 검색")
    void findByParticipant() {
        // given
        Friend friend = em.find(Friend.class, Long.valueOf(14));

        // when
        log.warn("친구 이름 = > " + friend.getName());
        List<Record> allRecord = recordRepository.findByParticipant(friend);

        // then
        for (Record record : allRecord) {
            Assertions.assertThat(record.getFriend()).isEqualTo(friend);
        }
    }

    @Test @DisplayName("Record - 특정 기록 검색")
    void findOneRecord() {
        // given
        Event event = em.find(Event.class, Long.valueOf(6));
        Friend friend = em.find(Friend.class, Long.valueOf(14));
        Record record = new Record(event, friend);
        em.persist(record);

        // when
        Record oneRecord = recordRepository.findOneRecord(event, friend);

        // then
        Assertions.assertThat(oneRecord.getId()).isEqualTo(record.getId());
    }
}