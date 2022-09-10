package projectsai.saibackend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import projectsai.saibackend.domain.enums.RelationStatus;
import projectsai.saibackend.domain.enums.RelationType;
import projectsai.saibackend.dto.friend.requestDto.AddFriendRequest;
import projectsai.saibackend.dto.friend.requestDto.DeleteFriendRequest;
import projectsai.saibackend.dto.friend.requestDto.UpdateFriendRequest;
import projectsai.saibackend.dto.friend.responseDto.FindFriendResponse;
import projectsai.saibackend.dto.friend.responseDto.FriendResultResponse;
import projectsai.saibackend.exception.ErrorCode;
import projectsai.saibackend.exception.ErrorResponse;
import projectsai.saibackend.repository.EventRepository;
import projectsai.saibackend.repository.FriendRepository;
import projectsai.saibackend.repository.RecordRepository;
import projectsai.saibackend.security.jwt.JwtProvider;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FriendService {

    @PersistenceContext
    EntityManager em;
    private final FriendRepository friendRepository;
    private final RecordRepository recordRepository;
    private final EventRepository eventRepository;

    private final MemberService memberService;
    private final JwtCookieService jwtCookieService;
    private final JwtProvider jwtProvider;
    private final ObjectMapper objectMapper;

    @Transactional // 친구 저장
    public boolean save(Friend friend) {
        try {
            friendRepository.addFriend(friend);
            log.info("Friend Service | addFriend() Success: 저장 성공");
            return true;
        }
        catch (Exception e) {
            log.error("Friend Service | addFriend() Fail: 에러 발생 => {}", e.getMessage());
            return false;
        }
    }

    // 친구 전체 검색
    public List<Friend> findAll(Member owner) {
        try {
            List<Friend> result = friendRepository.findAll(owner);
            log.info("Friend Service | findAll() Success: 검색 성공");
            return result;
        }
        catch (EmptyResultDataAccessException e) {
            log.warn("Friend Service | findAll() Fail: 검색 결과 없음 => {}", e.getMessage());
            return null;
        }
    }

    // 친구 ID 검색
    public Friend findById(Long id) {
        try {
            Friend friend = friendRepository.findById(id);
            log.info("Friend Service | findById() Success: 검색 성공");
            return friend;
        }
        catch (EmptyResultDataAccessException e) {
            log.warn("Friend Service | findById() Fail: 검색 결과 없음 => {}", e.getMessage());
            return null;
        }
    }

    // 친구 이름 검색
    public List<Friend> findByName(Member owner, String name) {
        try {
            List<Friend> friendList = friendRepository.findByName(owner, name);
            log.info("Friend Service | findByName() Success: 검색 성공");
            return friendList;
        }
        catch (EmptyResultDataAccessException e) {
            log.warn("Friend Service | findByName() Fail: 검색 결과 없음 => {}", e.getMessage());
            return null;
        }
    }

    // 친구 종류 검색
    public List<Friend> findByType(Member owner, RelationType type) {
        try {
            List<Friend> friendList = friendRepository.findByType(owner, type);
            log.info("Friend Service | findByType() Success: 검색 성공");
            return friendList;
        }
        catch (EmptyResultDataAccessException e) {
            log.warn("Friend Service | findByType() Fail: 검색 결과 없음 => {}", e.getMessage());
            return null;
        }
    }

    // 친구 상태 검색
    public List<Friend> findByStatus(Member owner, RelationStatus status) {
        try {
            List<Friend> friendList = friendRepository.findByStatus(owner, status);
            log.info("Friend Service | findByStatus() Success: 검색 성공");
            return friendList;
        }
        catch (EmptyResultDataAccessException e) {
            log.warn("Friend Service | findByStatus() Fail: 검색 결과 없음 => {}", e.getMessage());
            return null;
        }
    }

    // 다수의 Id를 이용한 친구 검색
    public List<Friend> findFriends(List<Long> friendIds) {
        try {
            List<Friend> friendList = friendRepository.findByIds(friendIds);
            log.info("Friend Service | findFriends() Success: 검색 성공");
            return friendList;
        }
        catch (Exception e) {
            log.warn("Friend Service | findFriends() Fail: 검색 결과 없음 => {}", e.getMessage());
            return null;
        }
    }

    @Transactional // 단일 친구 정보 변경
    public boolean updateFriendInfo(Long friendId, String name, RelationType type,
                                RelationStatus status, Integer score, String memo) {
        try {
            Friend findFriend = friendRepository.findById(friendId);
            findFriend.updateInfo(name, type, score, status, memo);
            findFriend.calcStatus();

            log.info("Friend Service | updateFriend() Success: 수정 완료");
            em.flush();
            em.clear();
            return true;
        }
        catch (EmptyResultDataAccessException e) {
            log.info("Friend Service | updateFriend() Fail: 존재하지 않는 ID => {}", e.getMessage());
            return false;
        }
    }

    @Transactional // 다수의 친구 점수를 복구
    public void restoreMultipleScore(List<Friend> prevParticipants, EventEvaluation prevEvaluation) {
        try {
            for (Friend friend : prevParticipants) {
                friend.restoreScore(prevEvaluation);
            }
            log.info("Friend Service | restoreScore() Success: 수정 성공");
        }
        catch (Exception e) {
            log.warn("Friend Service | restoreScore() Fail: 에러 발생 => {}", e.getMessage());
        }
    }

    @Transactional // 다수의 친구 점수 및 상태를 갱신
    public void renewMultipleScore(List<Friend> curnParticipants, EventEvaluation curnEvaluation) {
        try {
            for (Friend friend : curnParticipants) {
                friend.calcScore(curnEvaluation);
                friend.calcStatus();
            }
            log.info("Friend Service | renewMultipleScore() Success: 수정 성공");
        }
        catch (Exception e) {
            log.warn("Friend Service | renewMultipleScore() Success: 에러 발생 => {}", e.getMessage());
        }
    }

    @Transactional // 친구 객체의 점수를 갱신
    public void updateScoreStatus(Friend friend, EventEvaluation evaluation) {
        try {
            friend.calcScore(evaluation);
            friend.calcStatus();

            log.info("Friend Service | updateScoreStatus() Success: 친구 점수 업데이트");
        }
        catch (Exception e) {
            log.warn("Friend Service | updateScoreStatus() Success: 에러 발생 => {}", e.getMessage());
        }
    }

    @Transactional // 친구 삭제
    public boolean deleteFriend(Friend friend) {
        try {
            List<Record> friendList = recordRepository.findByParticipant(friend);
            Set<Event> eventSet = new HashSet<>();
            for (Record record : friendList) {
                recordRepository.deleteRecord(record);
                eventSet.add(record.getEvent());
            }

            for (Event event : eventSet) {
                if (recordRepository.findAll(event).size() == 0) {
                    eventRepository.deleteEvent(event);
                }
            }

            friendRepository.deleteFriend(friend);

            em.flush();
            em.clear();

            log.warn("Friend Service | deleteFriend() Success: 삭제 성공");
            return true;
        }
        catch (Exception e) {
            log.warn("Friend Service | deleteFriend() Fail: 에러 발생 => {}", e.getMessage());
            return false;
        }
    }

    // 친구 생성 시 관계 상태에 따라 점수 설정
    public int setInitialScore(RelationStatus status) {
        if (status.equals(RelationStatus.BAD)) return 10;
        else if (status.equals(RelationStatus.NEGATIVE)) return 30;
        else if (status.equals(RelationStatus.NORMAL)) return 50;
        else if (status.equals(RelationStatus.POSITIVE)) return 70;
        else return 90;
    }

    // FriendApi - 모든 친구 검색
    public void findAllFriendsApi(HttpServletRequest servletReq, HttpServletResponse servletResp) {

        servletResp.setContentType(APPLICATION_JSON_VALUE);

        String accessToken = jwtCookieService.getAccessToken(servletReq);
        String email = jwtProvider.getUserEmail(accessToken);

        try {
            List<Friend> friendList = this.findAll(memberService.findByEmail(email));

            List<FindFriendResponse> result = friendList.stream()
                    .map(FindFriendResponse::new).collect(toList());

            log.info("Friend Service | findAllFriendsApi() Success: 모든 친구 검색 완료");
            objectMapper.writeValue(servletResp.getOutputStream(), result);
        }
        catch (Exception e) {
            log.error("Friend Service | findAllFriendsApi() Fail: 오류 발생 => {}", e.getMessage());
        }
    }

    @Transactional // FriendApi - 친구 저장
    public void addFriendApi(AddFriendRequest requestDTO, HttpServletRequest servletReq,
                             HttpServletResponse servletResp) {

        servletResp.setContentType(APPLICATION_JSON_VALUE);

        try {
            String accessToken = jwtCookieService.getAccessToken(servletReq);
            String email = jwtProvider.getUserEmail(accessToken);
            Member owner = memberService.findByEmail(email);

            int score = this.setInitialScore(requestDTO.getStatus());
            Friend friend = new Friend(owner, requestDTO.getName(), requestDTO.getType(),
                    requestDTO.getStatus(), score, requestDTO.getMemo());

            if (this.save(friend)) {
                log.info("Friend Service | addFriendApi() Success: 친구 등록 완료");
                objectMapper.writeValue(servletResp.getOutputStream(), new FriendResultResponse(Boolean.TRUE));
            }
            else {
                log.info("Friend Service | addFriendApi() Fail: 친구 등록 실패");
                objectMapper.writeValue(servletResp.getOutputStream(), new ErrorResponse(ErrorCode.BAD_REQUEST));
            }
        }
        catch (Exception e) {
            log.error("Friend Service | addFriend() Fail: 에러 발생 => {}", e.getMessage());
        }
    }

    @Transactional // FriendApi - 친구 정보 수정
    public void updateFriendApi(UpdateFriendRequest requestDTO, HttpServletResponse servletResp) {
        servletResp.setContentType(APPLICATION_JSON_VALUE);

        try {
            Integer score = this.setInitialScore(requestDTO.getStatus());
            boolean result = this.updateFriendInfo(requestDTO.getFriendId(), requestDTO.getName(),
                    requestDTO.getType(), requestDTO.getStatus(), score, requestDTO.getMemo());

            if (result) {
                log.info("Friend Service | updateFriendApi() Success: 친구 업데이트 완료");
                objectMapper.writeValue(servletResp.getOutputStream(), new FriendResultResponse(Boolean.TRUE));
            }
            else {
                log.warn("Friend Service | updateFriendApi() Fail: 친구 업데이트 실패");
                servletResp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                objectMapper.writeValue(servletResp.getOutputStream(), new ErrorResponse(ErrorCode.BAD_REQUEST));
            }
        }
        catch (Exception e) {
            log.error("Friend Service | updateFriendApi() Fail: 에러 발생 => {}", e.getMessage());
        }
    }

    @Transactional // FriendApi - 친구 삭제(상태 수정)
    public void deleteFriendApi(DeleteFriendRequest requestDTO, HttpServletResponse servletResp) {

        servletResp.setContentType(APPLICATION_JSON_VALUE);

        try {
            Friend friend = this.findById(requestDTO.getFriendId());

            if (this.deleteFriend(friend)) {
                log.info("Friend Service | deleteFriendApi() Success: 친구 삭제 완료");
                objectMapper.writeValue(servletResp.getOutputStream(), new FriendResultResponse(Boolean.TRUE));
            } else {
                log.warn("Friend Service | deleteFriendApi() Fail: 친구 삭제 실패");
                servletResp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                objectMapper.writeValue(servletResp.getOutputStream(), new ErrorResponse(ErrorCode.BAD_REQUEST));
            }
        }
        catch (Exception e) {
            log.error("Friend Service | deleteFriendApi() Fail: 에러 발생 => {}", e.getMessage());
        }
    }
}
