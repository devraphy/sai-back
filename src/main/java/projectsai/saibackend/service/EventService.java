package projectsai.saibackend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import projectsai.saibackend.domain.Event;
import projectsai.saibackend.domain.Friend;
import projectsai.saibackend.domain.Member;
import projectsai.saibackend.domain.Record;
import projectsai.saibackend.domain.enums.EventEvaluation;
import projectsai.saibackend.domain.enums.EventPurpose;
import projectsai.saibackend.dto.event.requestDto.AddEventRequest;
import projectsai.saibackend.dto.event.requestDto.DeleteEventRequest;
import projectsai.saibackend.dto.event.requestDto.UpdateEventRequest;
import projectsai.saibackend.dto.event.responseDto.EventResultResponse;
import projectsai.saibackend.dto.event.responseDto.SearchEventResponse;
import projectsai.saibackend.exception.ErrorCode;
import projectsai.saibackend.exception.ErrorResponse;
import projectsai.saibackend.repository.EventRepository;
import projectsai.saibackend.repository.RecordRepository;
import projectsai.saibackend.security.jwt.JwtProvider;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EventService {

    @PersistenceContext
    EntityManager em;
    private final EventRepository eventRepository;
    private final RecordRepository recordRepository;
    private final MemberService memberService;
    private final FriendService friendService;
    private final RecordService recordService;
    private final JwtCookieService jwtCookieService;
    private final JwtProvider jwtProvider;
    private final ObjectMapper objectMapper;

    @Transactional // 이벤트 저장
    public Long addEvent(Event event) {
        try {
            Long savedEventId = eventRepository.addEvent(event);
            log.info("Event Service | addEvent() Success: 저장 성공");
            return savedEventId;
        } catch (Exception e) {
            log.warn("Event Service | addEvent() Fail: 에러 발생 => {}", e.getMessage());
            return null;
        }
    }

    // 이벤트 ID 검색
    public Event findById(Long eventId) {
        try {
            Event event = eventRepository.findById(eventId);
            log.info("Event Service | findById() Success: 검색 성공");
            return event;
        } catch (EmptyResultDataAccessException e) {
            log.warn("Event Service | findById() Fail: 검색 결과 없음 => {}", e.getMessage());
            return null;
        }
    }

    // 이벤트 전체 검색
    public List<Event> findAll(Member owner) throws Exception {
        try {
            List<Event> eventList = eventRepository.findAll(owner);
            log.info("Event Service | findAll() Success: 검색 성공");
            return eventList;
        } catch (EmptyResultDataAccessException e) {
            log.warn("Event Service | findAll() Fail: 검색 결과 없음 => {}", e.getMessage());
            return null;
        }
    }

    // 이벤트 이름 검색
    public List<Event> findByName(Member owner, String name) {
        try {
            List<Event> eventList = eventRepository.findByEventName(owner, name);
            log.info("Event Service | findByName() Success: 검색 성공");
            return eventList;
        } catch (EmptyResultDataAccessException e) {
            log.warn("Event Service | findByName() Fail: 검색 결과 없음 => {}", e.getMessage());
            return null;
        }
    }

    // 이벤트 날짜 검색
    public List<Event> findByDate(Member owner, LocalDate date) {
        try {
            List<Event> eventList = eventRepository.findByDate(owner, date);
            log.info("Event Service | findByDate() Success: 검색 성공");
            return eventList;
        } catch (EmptyResultDataAccessException e) {
            log.warn("Event Service | findByDate() Fail: 검색 결과 없음 => {}", e.getMessage());
            return null;
        }
    }

    // 이벤트 목적 검색
    public List<Event> findByPurpose(Member owner, EventPurpose purpose) {
        try {
            List<Event> eventList = eventRepository.findByPurpose(owner, purpose);
            log.info("Event Service | findByPurpose() Success: 검색 성공");
            return eventList;
        } catch (EmptyResultDataAccessException e) {
            log.warn("Event Service | findByPurpose() Fail: 검색 결과 없음=> {}", e.getMessage());
            return null;
        }
    }

    // 이벤트 평가 검색
    public List<Event> findByEvaluation(Member owner, EventEvaluation evaluation) {
        try {
            List<Event> eventList = eventRepository.findByEvaluation(owner, evaluation);
            log.info("Event Service | findByEvaluation() Success: 검색 성공");
            return eventList;
        } catch (EmptyResultDataAccessException e) {
            log.warn("Event Service | findByEvaluation() Fail: 검색 결과 없음 => {}", e.getMessage());
            return null;
        }
    }

    @Transactional // 이벤트 정보 수정
    public boolean updateEvent(Long eventId, String name, LocalDate date,
                               EventPurpose purpose, EventEvaluation evaluation) {
        try {
            Event findEvent = eventRepository.findById(eventId);
            findEvent.updateInfo(name, date, purpose, evaluation);

            log.info("Event Service | updateEvent() Success: 수정 성공");

            em.flush();
            em.clear();
            return true;
        } catch (Exception e) {
            log.warn("Event Service | updateEvent() Fail: 에러 발생=> {}", e.getMessage());
            return false;
        }
    }

    @Transactional // 이벤트 삭제
    public boolean deleteEvent(Event event) {
        try {
            List<Record> recordList = recordRepository.findAll(event);
            List<Friend> friendList = recordList.stream().map(Record::getFriend).collect(Collectors.toList());
            for (Friend friend : friendList) {
                friend.restoreScore(event.getEvaluation());
                friend.calcStatus();
            }
            recordRepository.deleteAllRecords(event);
            eventRepository.deleteEvent(event);

            log.info("Event Service | deleteEvent() Success: 삭제 성공");

            em.flush();
            em.clear();
            return true;
        } catch (Exception e) {
            log.warn("Event Service | deleteEvent() Fail: 에러 발생 => {}", e.getMessage());
            return false;
        }
    }

    // EventApi - 모든 이벤트 검색
    public void searchEventApi(HttpServletRequest servletReq, HttpServletResponse servletResp) {

        servletResp.setContentType(APPLICATION_JSON_VALUE);
        objectMapper.registerModule(new JavaTimeModule());

        try {
            String accessToken = jwtCookieService.getAccessToken(servletReq);
            String email = jwtProvider.getUserEmail(accessToken);
            Member owner = memberService.findByEmail(email);

            List<Event> allEvents = this.findAll(owner);
            List<SearchEventResponse> result = new ArrayList<>();

            for (Event event : allEvents) {
                List<Record> recordList = recordService.findAll(event);
                List<Long> friendIds = recordList.stream()
                        .map(o -> o.getFriend().getFriendId()).collect(Collectors.toList());
                List<Friend> participants = friendService.findFriends(friendIds);
                result.add(new SearchEventResponse(event.getEventId(), event.getName(), event.getDate(),
                        event.getPurpose(), event.getEvaluation(), participants));
            }

            log.info("Event Service | searchEventApi() Success: 이벤트 검색 성공");
            objectMapper.writeValue(servletResp.getOutputStream(), result);
        } catch (Exception e) {
            log.error("Event Service | searchEventApi() Fail: 오류 발생 => {}", e.getMessage());
        }
    }

    @Transactional // EventApi - 이벤트 저장
    public void addEventApi(AddEventRequest requestDTO, HttpServletRequest servletReq, HttpServletResponse servletResp) {
        servletResp.setContentType(APPLICATION_JSON_VALUE);

        try {
            String accessToken = jwtCookieService.getAccessToken(servletReq);
            String email = jwtProvider.getUserEmail(accessToken);
            Member owner = memberService.findByEmail(email);

            Event event = new Event(owner, requestDTO.getDate(), requestDTO.getPurpose(),
                    requestDTO.getName(), requestDTO.getEvaluation());

            this.addEvent(event);

            for (Long id : requestDTO.getParticipants()) {
                Friend friend = friendService.findById(id);
                recordService.addRecord(new Record(event, friend));
                friendService.updateScoreStatus(friend, requestDTO.getEvaluation());
            }

            log.info("Event Service | addEventApi() Success: 이벤트 저장 성공");
            objectMapper.writeValue(servletResp.getOutputStream(), new EventResultResponse(Boolean.TRUE));
        } catch (Exception e) {
            log.error("Event Service | addEventApi() Fail: 에러 발생 => {}", e.getMessage());
        }
    }

    @Transactional // EventApi - 이벤트 수정
    public void updateEventApi(UpdateEventRequest requestDTO, HttpServletResponse servletResp) {

        servletResp.setContentType(APPLICATION_JSON_VALUE);

        try {
            Event event = this.findById(requestDTO.getEventId());
            EventEvaluation postEvaluation = requestDTO.getEvaluation();
            EventEvaluation prevEvaluation = event.getEvaluation();
            List<Long> updatedParticipantsIds = requestDTO.getParticipants();
            List<Record> recordList = recordService.findAll(event);
            List<Friend> postParticipants = friendService.findFriends(updatedParticipantsIds);
            List<Friend> prevParticipants = recordList.stream()
                    .map(Record::getFriend).collect(Collectors.toList());

            if (postParticipants.equals(prevParticipants) && !postEvaluation.equals(prevEvaluation)) {
                friendService.restoreMultipleScore(prevParticipants, prevEvaluation);
                friendService.renewMultipleScore(postParticipants, postEvaluation);
            } else if (!postParticipants.equals(prevParticipants)) {
                friendService.restoreMultipleScore(prevParticipants, prevEvaluation);
                recordService.deleteAllRecords(event);
                recordService.addMultipleRecords(event, postParticipants);
                friendService.renewMultipleScore(postParticipants, postEvaluation);
            }

            boolean result = this.updateEvent(event.getEventId(), requestDTO.getName(), requestDTO.getDate(),
                    requestDTO.getPurpose(), requestDTO.getEvaluation());

            if (result) {
                log.info("Event Service | updateEventApi() Success: 이벤트 업데이트 성공");
                objectMapper.writeValue(servletResp.getOutputStream(), new EventResultResponse(Boolean.TRUE));
            } else {
                log.warn("Event Service | updateEventApi() Fail: 이벤트 업데이트 실패");
                objectMapper.writeValue(servletResp.getOutputStream(), new ErrorResponse(ErrorCode.BAD_REQUEST));
            }
        } catch (Exception e) {
            log.error("Event Service | updateEventApi() Fail: 에러 발생 => {}", e.getMessage());
        }

    }

    @Transactional // EventApi - 이벤트 삭제
    public void deleteEventApi(DeleteEventRequest requestDTO, HttpServletResponse servletResp) {
        servletResp.setContentType(APPLICATION_JSON_VALUE);

        try {
            Event event = this.findById(requestDTO.getEventId());
            boolean result = this.deleteEvent(event);

            if (result) {
                log.info("Event Service | deleteEventApi() Success: 이벤트 삭제 완료");
                objectMapper.writeValue(servletResp.getOutputStream(), new EventResultResponse(Boolean.TRUE));
            } else {
                log.warn("Event Service | deleteEventApi() Fail: 이벤트 삭제 실패");
                servletResp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                objectMapper.writeValue(servletResp.getOutputStream(), new ErrorResponse(ErrorCode.BAD_REQUEST));
            }
        } catch (Exception e) {
            log.error("Event Service | deleteEventApi() Fail: 에러 발생 => {}", e.getMessage());
        }
    }

}
