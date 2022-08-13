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

    // 단일 이벤트 참가자 기록 저장
    public boolean addRecord(Record record) {
        try {
            recordRepository.addRecord(record);
            log.info("Record | addRecord() Success: 저장 성공");
            return true;
        }
        catch(Exception e) {
            log.warn("Record | addRecord() Fail: 에러 발생 => " + e.getMessage());
            return false;
        }
    }

    // 다수의 이벤트 참가자 기록 저장
    public boolean addMultipleRecords(Event event, List<Friend> curnParticipants) {
        try {
            for (Friend friend : curnParticipants) {
                recordRepository.addRecord(new Record(event, friend));
            }
            log.info("Record | addMultipleRecords() Success: 저장 성공");
            return true;
        }
        catch (Exception e) {
            log.warn("Record | addMultipleRecords() Fail: 에러 발생 => " + e.getMessage());
            return false;
        }
    }

    // 특정 이벤트의 모든 참가자 기록을 검색
    public List<Record> findAll(Event event) {
        try {
            List<Record> recordList = recordRepository.findAll(event);
            log.info("Record | findAllParticipants() Success: 검색 성공");
            return recordList;
        }
        catch(EmptyResultDataAccessException e) {
            log.warn("Record | findAllParticipants() Fail: 검색 결과 없음 => " + e.getMessage());
            return null;
        }
    }

    // 특정 참가자가 포함된 모든 이벤트 검색
    public List<Record> findByParticipant(Friend friend) {
        try {
            List<Record> recordList = recordRepository.findByParticipant(friend);
            log.info("Record | findByParticipant() Success: 검색 성공");
            return recordList;
        }
        catch(EmptyResultDataAccessException e) {
            log.warn("Record | findByParticipant() Fail: 검색 결과 없음 => " + e.getMessage());
            return null;
        }
    }

    // 특정한 기록을 검색
    public Record findOne(Event event, Friend friend) {
        try {
            Record record = recordRepository.findOne(event, friend);
            log.info("Record | findOneRecord() Success: 검색 성공");
            return record;
        }
        catch(EmptyResultDataAccessException e) {
            log.warn("Record | findOneRecord() Fail: 검색 결과 없음 => " + e.getMessage());
            return null;
        }
    }

    // 특정 이벤트의 모든 참가자 기록을 삭제
    @Transactional
    public boolean deleteAllRecords(Event event) {
        try {
            int result = recordRepository.deleteAllRecords(event);

            if(result < 1) {
                log.warn("Record | deleteAllRecord() Fail: 삭제된 데이터가 없음");
                return false;
            }

            em.flush();
            em.clear();
            log.info("Record | deleteAllRecord() Success: 삭제 성공");
            return true;
        }
        catch (Exception e) {
            log.warn("Record | deleteAllRecord() Fail: 에러 발생 => " + e.getMessage());
            return false;
        }
    }

    // 특정 기록을 삭제
    @Transactional
    public boolean deleteRecord(Record record) {
        try {
            recordRepository.deleteRecord(record);
            em.flush();
            em.clear();
            log.info("Record | deleteRecordById() Success: 삭제 성공");
            return true;
        }
        catch(Exception e) {
            log.warn("Record | deleteRecordById() Fail: 에러 발생 => " + e.getMessage());
            return false;
        }
    }
}
