package projectsai.saibackend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import projectsai.saibackend.domain.Event;
import projectsai.saibackend.domain.Friend;
import projectsai.saibackend.domain.Record;
import projectsai.saibackend.repository.RecordRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor @Slf4j
public class RecordService {

    @PersistenceContext EntityManager em;
    private final RecordRepository recordRepository;

    // 이벤트 참가자 기록 저장
    public boolean addRecord(Record record) {
        try {
            recordRepository.save(record);
            log.info("addRecord Success: 기록 저장 완료");
            return true;
        } catch(Exception e) {
            log.warn("addRecord Fail: 기록 저장 실패");
            return false;
        }
    }

    // 특정 이벤트의 모든 참가자 검색
    public List<Record> findAllParticipants(Event event) {
        return recordRepository.findAllParticipants(event);
    }

    // 특정 참가자가 포함된 모든 이벤트 검색
    public List<Record> findByParticipant(Friend friend) {
        return recordRepository.findByParticipant(friend);
    }

    // 특정한 기록을 검색
    public Record findOneRecord(Event event, Friend friend) {
        return recordRepository.findOneRecord(event, friend);
    }

    // 연관된 Event의 모든 기록을 삭제
    @Transactional
    public boolean deleteAllRecord(Event event) {
        int result = recordRepository.deleteAllRecord(event);
        if(result > 0) {
            return true;
        }
        return false;
    }

    @Transactional
    public boolean deleteRecordById(Record record) {
        try {
            recordRepository.deleteRecordById(record);
        } catch(Exception e) {
            log.warn("deleteRecordById Fail: 기록 삭제 실패 => " + e.getMessage());
            return false;
        }
        return true;
    }
//    SELECT * FROM MEMBER ;
//    SELECT * FROM FRIEND  ;
//    SELECT * FROM EVENT  ;
//    SELECT * FROM RECORD  ;
}
