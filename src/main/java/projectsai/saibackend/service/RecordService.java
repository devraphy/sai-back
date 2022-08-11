package projectsai.saibackend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
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
            recordRepository.addRecord(record);
            log.info("addRecord() Success: 기록 저장 완료");
            return true;
        }
        catch(Exception e) {
            log.warn("addRecord() Fail: 기록 저장 실패 => " + e.getMessage());
            return false;
        }
    }

    // 특정 이벤트의 모든 참가자 기록을 검색
    public List<Record> findAll(Event event) {
        try {
            List<Record> recordList = recordRepository.findAll(event);
            log.info("findAllParticipants() Success: 특정 이벤트의 모든 참가자 기록을 검색 성공");
            return recordList;
        }
        catch(EmptyResultDataAccessException e) {
            log.warn("findAllParticipants() Fail: 특정 이벤트의 모든 참가자 기록 검색 실패 => " + e.getMessage());
            return null;
        }
    }

    // 특정 참가자가 포함된 모든 이벤트 검색
    public List<Record> findByParticipant(Friend friend) {
        try {
            List<Record> recordList = recordRepository.findByParticipant(friend);
            log.info("findByParticipant() Success: 특정 참가자가 포함된 모든 이벤트 기록 검색 성공");
            return recordList;
        }
        catch(EmptyResultDataAccessException e) {
            log.warn("findByParticipant() Fail: 특정 참가자가 포함된 모든 이벤트 기록 검색 실패 => " + e.getMessage());
            return null;
        }
    }

    // 특정한 기록을 검색
    public Record findOne(Event event, Friend friend) {
        try {
            Record record = recordRepository.findOne(event, friend);
            log.info("findOneRecord() Success: 특정 기록 검색 성공");
            return record;
        }
        catch(EmptyResultDataAccessException e) {
            log.warn("findOneRecord() Fail: 특정 기록 검색 실패 => " + e.getMessage());
            return null;
        }
    }

    // 특정 이벤트의 모든 참가자 기록을 삭제
    @Transactional
    public boolean deleteAllRecord(Event event) {
        int result = recordRepository.deleteAllRecord(event);
        if(result == 0) {
            log.warn("deleteAllRecord() Fail: 기록 삭제 실패 => 삭제된 데이터가 없음");
            return false;
        }
        log.info("deleteAllRecord() Success: 기록 삭제 성공");
        return true;
    }

    // 특정 기록을 삭제
    @Transactional
    public boolean deleteRecord(Record record) {
        try {
            recordRepository.deleteRecord(record);
            log.info("deleteRecordById() Success: 기록 삭제 성공");
            return true;
        }
        catch(Exception e) {
            log.warn("deleteRecordById() Fail: 기록 삭제 실패 => " + e.getMessage());
            return false;
        }
    }
}
