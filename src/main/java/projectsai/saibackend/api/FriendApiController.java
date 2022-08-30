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
import projectsai.saibackend.service.JwtCookieService;
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
    private final JwtCookieService jwtCookieService;
    private final JwtProvider jwtProvider;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/add") // 친구 추가
    public void addFriend(@RequestBody @Valid AddFriendRequest requestDTO,
                          HttpServletRequest servletReq, HttpServletResponse servletResp) throws IOException {

        servletResp.setContentType(APPLICATION_JSON_VALUE);

        if(jwtCookieService.validateAccessToken(servletReq)) {
            int score = friendService.setInitialScore(requestDTO.getStatus());
            Member owner = em.find(Member.class, requestDTO.getOwnerId());
            Friend friend = new Friend(owner, requestDTO.getName(), requestDTO.getType(),
                    requestDTO.getStatus(), score, requestDTO.getMemo(), requestDTO.getBirthDate());

            if(friendService.addFriend(friend)) {
                log.info("Friend API | addFriend() Success: 친구 저장 성공");
                objectMapper.writeValue(servletResp.getOutputStream(), new AddFriendResponse(Boolean.TRUE));
                return;
            }
        }
        log.warn("Friend API | addFriend() Fail: 친구 저장 실패");
        servletResp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        objectMapper.writeValue(servletResp.getOutputStream(), new AddFriendResponse(Boolean.FALSE));
    }

    @GetMapping("/search") // 모든 친구 검색
    public void findAll(HttpServletRequest servletReq, HttpServletResponse servletResp) throws IOException {

        if(jwtCookieService.validateAccessToken(servletReq)) {
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
        }
        log.info("Friend API | findAll() Fail: 친구 검색 실패");
        servletResp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        objectMapper.writeValue(servletResp.getOutputStream(),new FriendResultResponse(Boolean.FALSE));
    }

    @PutMapping("/update") // 친구 수정
    public void updateFriend(@RequestBody @Valid UpdateFriendRequest requestDTO,
                             HttpServletRequest servletReq, HttpServletResponse servletResp) throws IOException {

        if(jwtCookieService.validateAccessToken(servletReq)) {
            try {
                Integer score = friendService.setInitialScore(requestDTO.getStatus());
                friendService.updateFriend(requestDTO.getFriendId(), requestDTO.getName(), requestDTO.getType(),
                        requestDTO.getStatus(), score, requestDTO.getMemo(), requestDTO.getBirthDate());
            }
            catch(Exception e) {
                log.info("Friend API | updateFriend() Fail: 친구 수정 실패");
                servletResp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                objectMapper.writeValue(servletResp.getOutputStream(), new FriendResultResponse(Boolean.FALSE));
                return;
            }
        }
        log.info("Friend API | updateFriend() Success: 친구 수정 성공");
        objectMapper.writeValue(servletResp.getOutputStream(), new FriendResultResponse(Boolean.TRUE));
    }

    @DeleteMapping("/delete")
    public void deleteFriend(@RequestBody @Valid DeleteFriendRequest request,
                             HttpServletRequest servletReq, HttpServletResponse servletResp) throws IOException {

        if(jwtCookieService.validateAccessToken(servletReq)) {
            Friend friend = friendService.findById(request.getFriendId());
            if(friendService.deleteFriend(friend)) {
                log.info("Friend API | deleteFriend() Success: 친구 삭제 성공");
                objectMapper.writeValue(servletResp.getOutputStream(), new FriendResultResponse(Boolean.TRUE));
                return;
            }
        }
        log.warn("Friend API | deleteFriend() Fail: 친구 삭제 실패");
        servletResp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        objectMapper.writeValue(servletResp.getOutputStream(), new FriendResultResponse(Boolean.FALSE));
    }
}
