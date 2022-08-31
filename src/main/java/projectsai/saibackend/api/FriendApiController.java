package projectsai.saibackend.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import projectsai.saibackend.domain.Friend;
import projectsai.saibackend.domain.Member;
import projectsai.saibackend.dto.friend.requestDto.AddFriendRequest;
import projectsai.saibackend.dto.friend.requestDto.DeleteFriendRequest;
import projectsai.saibackend.dto.friend.requestDto.UpdateFriendRequest;
import projectsai.saibackend.dto.friend.responseDto.*;
import projectsai.saibackend.security.jwt.JwtProvider;
import projectsai.saibackend.service.FriendService;
import projectsai.saibackend.service.MemberService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

import static java.util.stream.Collectors.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController @Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/friend")
public class FriendApiController {

    @PersistenceContext EntityManager em;
    private final FriendService friendService;
    private final MemberService memberService;
    private final JwtProvider jwtProvider;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/add") // 친구 추가
    public void addFriend(@RequestBody @Valid AddFriendRequest requestDTO,
                          HttpServletResponse servletResp) throws IOException {

        servletResp.setContentType(APPLICATION_JSON_VALUE);

        try {
            int score = friendService.setInitialScore(requestDTO.getStatus());
            Member owner = em.find(Member.class, requestDTO.getOwnerId());
            Friend friend = new Friend(owner, requestDTO.getName(), requestDTO.getType(),
                    requestDTO.getStatus(), score, requestDTO.getMemo(), requestDTO.getBirthDate());

            if(friendService.addFriend(friend)) {
                log.info("Friend API | addFriend() Success: 친구 저장 성공");
                objectMapper.writeValue(servletResp.getOutputStream(), new FriendResultResponse(Boolean.TRUE));
                return;
            }
        }
        catch (Exception e) {
            log.error("Friend API | addFriend() Fail: 에러 발생 => {}", e.getMessage());
        }

        log.warn("Friend API | findAll() Fail: 친구 검색 실패");
        servletResp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        objectMapper.writeValue(servletResp.getOutputStream(), new FriendResultResponse(Boolean.FALSE));
    }

    @GetMapping("/search") // 모든 친구 검색
    public void findAll(HttpServletRequest servletReq, HttpServletResponse servletResp) throws IOException {

        servletResp.setContentType(APPLICATION_JSON_VALUE);

        Cookie[] cookies = servletReq.getCookies();
        String accessToken = cookies[0].getValue();
        String email = jwtProvider.getUserEmail(accessToken);

        try {
            List<Friend> friendList = friendService.findAll(memberService.findByEmail(email));

            List<FindFriendResponse> result = friendList.stream()
                    .map(FindFriendResponse::new).collect(toList());

            log.info("Friend API | findAll() Success: 모든 친구 검색 성공");
            objectMapper.writeValue(servletResp.getOutputStream(), result);
            return;
        }
        catch(Exception e) {
            log.error("Friend API | findAll() Fail: 오류 발생 => {}", e.getMessage());
        }

        log.warn("Friend API | findAll() Fail: 친구 검색 실패");
        servletResp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        objectMapper.writeValue(servletResp.getOutputStream(), new FriendResultResponse(Boolean.FALSE));
    }

    @PutMapping("/update") // 친구 수정
    public void updateFriend(@RequestBody @Valid UpdateFriendRequest requestDTO,
                             HttpServletResponse servletResp) throws IOException {

        servletResp.setContentType(APPLICATION_JSON_VALUE);

        try {
            Integer score = friendService.setInitialScore(requestDTO.getStatus());
            boolean result = friendService.updateFriend(requestDTO.getFriendId(), requestDTO.getName(), requestDTO.getType(),
                    requestDTO.getStatus(), score, requestDTO.getMemo(), requestDTO.getBirthDate());

            if(result) {
                log.info("Friend API | updateFriend() Success: 친구 업데이트 성공");
                objectMapper.writeValue(servletResp.getOutputStream(), new FriendResultResponse(Boolean.TRUE));
                return;
            }
            log.warn("Friend API | updateFriend() Success: 친구 업데이트 실패");
        }
        catch(Exception e) {
            log.error("Friend API | updateFriend() Fail: 에러 발생 => {}", e.getMessage());
        }

        log.warn("Friend API | updateFriend() Success: 친구 업데이트 실패(모든 토큰 만료)");
        servletResp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        objectMapper.writeValue(servletResp.getOutputStream(), new FriendResultResponse(Boolean.FALSE));
    }

    @DeleteMapping("/delete")
    public void deleteFriend(@RequestBody @Valid DeleteFriendRequest request,
                             HttpServletResponse servletResp) throws IOException {

        servletResp.setContentType(APPLICATION_JSON_VALUE);

        try {
            Friend friend = friendService.findById(request.getFriendId());

            if(friendService.deleteFriend(friend)) {
                log.info("Friend API | deleteFriend() Success: 친구 삭제 성공");
                objectMapper.writeValue(servletResp.getOutputStream(), new FriendResultResponse(Boolean.TRUE));
                return;
            }
        }
        catch (Exception e) {
            log.error("Friend API | deleteFriend() Fail: 에러 발생 => {}", e.getMessage());
        }

        log.warn("Friend API | deleteFriend() Fail: 친구 삭제 실패");
        servletResp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        objectMapper.writeValue(servletResp.getOutputStream(), new FriendResultResponse(Boolean.FALSE));
    }
}
