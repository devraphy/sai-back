package projectsai.saibackend.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import projectsai.saibackend.domain.Event;
import projectsai.saibackend.domain.Friend;
import projectsai.saibackend.domain.Member;
import projectsai.saibackend.domain.Record;
import projectsai.saibackend.domain.enums.EventEvaluation;
import projectsai.saibackend.dto.event.requestDto.AddEventRequest;
import projectsai.saibackend.dto.event.requestDto.DeleteEventRequest;
import projectsai.saibackend.dto.event.requestDto.UpdateEventRequest;
import projectsai.saibackend.dto.event.responseDto.EventResultResponse;
import projectsai.saibackend.dto.event.responseDto.SearchEventResponse;
import projectsai.saibackend.security.jwt.JwtProvider;
import projectsai.saibackend.service.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController @Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/event")
@ApiResponses({@ApiResponse(code = 500, message = "에러 발생"), @ApiResponse(code = 401, message = "토큰 검증 실패")})
public class EventApiController {

    @PersistenceContext EntityManager em;
    private final EventService eventService;
    private final MemberService memberService;
    private final FriendService friendService;
    private final RecordService recordService;
    private final JwtCookieService jwtCookieService;
    private final JwtProvider jwtProvider;
    private final ObjectMapper objectMapper = new ObjectMapper();


    @ApiOperation(value = "이벤트 등록")
    @ApiResponse(code = 200, message = "이벤트 등록 성공")
    @PostMapping
    public void addEvent(@RequestBody @Valid AddEventRequest requestDTO,
                         HttpServletRequest servletReq, HttpServletResponse servletResp) throws IOException {

        servletResp.setContentType(APPLICATION_JSON_VALUE);

        try {
            String accessToken = jwtCookieService.getAccessToken(servletReq);
            String email = jwtProvider.getUserEmail(accessToken);
            Member owner = memberService.findByEmail(email);

            Event event = new Event(owner, requestDTO.getDate(), requestDTO.getPurpose(),
                    requestDTO.getName(), requestDTO.getEvaluation());

            eventService.addEvent(event);

            for (Long id : requestDTO.getParticipants()) {
                Friend friend = friendService.findById(id);
                recordService.addRecord(new Record(event, friend));
                friendService.updateScoreStatus(friend, requestDTO.getEvaluation());
            }
            log.info("Event API | addEvent() Success: 이벤트 저장 성공");
            objectMapper.writeValue(servletResp.getOutputStream(), new EventResultResponse(Boolean.TRUE));
        }
        catch(Exception e) {
            log.error("Event API | addEvent() Fail: 에러 발생 => {}", e.getMessage());
            servletResp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(servletResp.getOutputStream(), new EventResultResponse(Boolean.TRUE));
        }
    }

    @ApiOperation(value = "전체 이벤트 검색", notes = "사용자 소유의 모든 이벤트 검색")
    @ApiResponse(code = 200, message = "검색 성공")
    @GetMapping
    public void searchEvents(HttpServletRequest servletReq, HttpServletResponse servletResp) throws IOException {

        servletResp.setContentType(APPLICATION_JSON_VALUE);
        objectMapper.registerModule(new JavaTimeModule());

        try {
            String accessToken = jwtCookieService.getAccessToken(servletReq);
            String email = jwtProvider.getUserEmail(accessToken);
            Member owner = memberService.findByEmail(email);

            List<Event> allEvents = eventService.findAll(owner);
            List<SearchEventResponse> result = new ArrayList<>();

            for (Event event : allEvents) {
                List<Record> recordList = recordService.findAll(event);
                List<Long> friendIds = recordList.stream()
                        .map(o -> o.getFriend().getFriendId()).collect(Collectors.toList());
                List<Friend> friendList = friendService.findFriends(friendIds);
                result.add(new SearchEventResponse(event, friendList));
            }

            log.info("Event API | searchEvents() Success: 이벤트 검색 성공");
            objectMapper.writeValue(servletResp.getOutputStream(), result);
        }
        catch(Exception e) {
            log.error("Event API | searchEvents() Fail: 오류 발생 => {}", e.getMessage());
            servletResp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(servletResp.getOutputStream(), new EventResultResponse(Boolean.FALSE));
        }
    }

    @ApiOperation(value = "이벤트 변경")
    @ApiResponses({@ApiResponse(code = 200, message = "변경 완료"), @ApiResponse(code = 400, message = "변경 실패")})
    @PutMapping
    public void updateEvent(@RequestBody @Valid UpdateEventRequest requestDTO,
                            HttpServletResponse servletResp) throws IOException {

        servletResp.setContentType(APPLICATION_JSON_VALUE);

        try {
            Event event = eventService.findById(requestDTO.getEventId());
            EventEvaluation postEvaluation = requestDTO.getEvaluation();
            EventEvaluation prevEvaluation = event.getEvaluation();
            List<Long> updatedParticipantsIds = requestDTO.getParticipants();
            List<Record> recordList = recordService.findAll(event);
            List<Friend> postParticipants = friendService.findFriends(updatedParticipantsIds);
            List<Friend> prevParticipants = recordList.stream()
                    .map(Record::getFriend).collect(Collectors.toList());

            if(postParticipants.equals(prevParticipants) && !postEvaluation.equals(prevEvaluation)) {
                friendService.restoreMultipleScore(prevParticipants, prevEvaluation);
                friendService.renewMultipleScore(postParticipants, postEvaluation);
            }

            else if(!postParticipants.equals(prevParticipants)) {
                friendService.restoreMultipleScore(prevParticipants, prevEvaluation);
                recordService.deleteAllRecords(event);
                recordService.addMultipleRecords(event, postParticipants);
                friendService.renewMultipleScore(postParticipants, postEvaluation);
            }
            boolean result = eventService.updateEvent(event.getEventId(), requestDTO.getName(), requestDTO.getDate(),
                    requestDTO.getPurpose(), requestDTO.getEvaluation());

            if(result) {
                log.info("Event API | updateEvent() Success: 이벤트 업데이트 성공");
                objectMapper.writeValue(servletResp.getOutputStream(), new EventResultResponse(Boolean.TRUE));
                return;
            }
            else {
                log.warn("Event API | updateEvent() Fail: 이벤트 업데이트 실패");
                servletResp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        }
        catch (Exception e) {
            log.error("Event API | updateEvent() Fail: 에러 발생 => {}", e.getMessage());
            servletResp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        objectMapper.writeValue(servletResp.getOutputStream(), new EventResultResponse(Boolean.FALSE));
    }

    @ApiOperation(value = "이벤트 삭제")
    @ApiResponses({@ApiResponse(code = 200, message = "삭제 완료"), @ApiResponse(code = 400, message = "삭제 실패")})
    @DeleteMapping // 이벤트 - 삭제
    public void deleteEvent(@RequestBody @Valid DeleteEventRequest requestDTO,
                            HttpServletResponse servletResp) throws IOException {

        servletResp.setContentType(APPLICATION_JSON_VALUE);

        try {
            Event event = em.find(Event.class, requestDTO.getEventId());
            boolean result = eventService.deleteEvent(event);
            if(result) {
                log.info("Event API | deleteEvent() Success: 이벤트 삭제 완료");
                objectMapper.writeValue(servletResp.getOutputStream(), new EventResultResponse(Boolean.TRUE));
                return;
            }
            else {
                log.warn("Event API | deleteEvent() Fail: 이벤트 삭제 실패");
                servletResp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        }
        catch (Exception e) {
            log.error("Event API | deleteEvent() Fail: 에러 발생 => {}", e.getMessage());
            servletResp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        objectMapper.writeValue(servletResp.getOutputStream(), new EventResultResponse(Boolean.FALSE));
    }
}