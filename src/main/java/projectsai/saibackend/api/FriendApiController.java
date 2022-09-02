package projectsai.saibackend.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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

@Api(tags = "Friend API")
@RestController @Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/friend")
@ApiResponses({@ApiResponse(code = 500, message = "에러 발생"), @ApiResponse(code = 401, message = "토큰 검증 실패")})
public class FriendApiController {

    private final FriendService friendService;
    private final MemberService memberService;
    private final JwtCookieService jwtCookieService;
    private final JwtProvider jwtProvider;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @ApiOperation(value = "친구 등록")
    @ApiResponse(code = 200, message = "친구 등록 완료")
    @PostMapping
    public void addFriend(@RequestBody @Valid AddFriendRequest requestDTO,
                          HttpServletRequest servletReq, HttpServletResponse servletResp) throws IOException {

        servletResp.setContentType(APPLICATION_JSON_VALUE);

        try {
            String accessToken = jwtCookieService.getAccessToken(servletReq);
            String email = jwtProvider.getUserEmail(accessToken);
            Member owner = memberService.findByEmail(email);

            int score = friendService.setInitialScore(requestDTO.getStatus());
            Friend friend = new Friend(owner, requestDTO.getName(), requestDTO.getType(),
                    requestDTO.getStatus(), score, requestDTO.getMemo(), requestDTO.getBirthDate());

            if(friendService.addFriend(friend)) {
                log.info("Friend API | addFriend() Success: 친구 등록 완료");
                objectMapper.writeValue(servletResp.getOutputStream(), new FriendResultResponse(Boolean.TRUE));
            }
        }
        catch (Exception e) {
            log.error("Friend API | addFriend() Fail: 에러 발생 => {}", e.getMessage());
            servletResp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(servletResp.getOutputStream(), new FriendResultResponse(Boolean.FALSE));
        }
    }

    @ApiOperation(value = "모든 친구 검색", notes = "사용자 소유의 모든 친구 검색")
    @ApiResponse(code = 200, message = "검색 완료")
    @GetMapping
    public void findAll(HttpServletRequest servletReq, HttpServletResponse servletResp) throws IOException {

        servletResp.setContentType(APPLICATION_JSON_VALUE);

        String accessToken = jwtCookieService.getAccessToken(servletReq);
        String email = jwtProvider.getUserEmail(accessToken);

        try {
            List<Friend> friendList = friendService.findAll(memberService.findByEmail(email));

            List<FindFriendResponse> result = friendList.stream()
                    .map(FindFriendResponse::new).collect(toList());

            log.info("Friend API | findAll() Success: 모든 친구 검색 완료");
            objectMapper.writeValue(servletResp.getOutputStream(), result);
        }
        catch(Exception e) {
            log.error("Friend API | findAll() Fail: 오류 발생 => {}", e.getMessage());
            servletResp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(servletResp.getOutputStream(), new FriendResultResponse(Boolean.FALSE));
        }
    }

    @ApiOperation(value = "친구 정보 변경")
    @ApiResponses({@ApiResponse(code = 200, message = "변경 완료"), @ApiResponse(code = 400, message = "변경 실패")})
    @PutMapping
    public void updateFriend(@RequestBody @Valid UpdateFriendRequest requestDTO,
                             HttpServletResponse servletResp) throws IOException {

        servletResp.setContentType(APPLICATION_JSON_VALUE);

        try {
            Integer score = friendService.setInitialScore(requestDTO.getStatus());
            boolean result = friendService.updateFriend(requestDTO.getFriendId(), requestDTO.getName(), requestDTO.getType(),
                    requestDTO.getStatus(), score, requestDTO.getMemo(), requestDTO.getBirthDate());

            if(result) {
                log.info("Friend API | updateFriend() Success: 친구 업데이트 완료");
                objectMapper.writeValue(servletResp.getOutputStream(), new FriendResultResponse(Boolean.TRUE));
                return;
            }
            log.warn("Friend API | updateFriend() Fail: 친구 업데이트 실패");
            servletResp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(servletResp.getOutputStream(), new FriendResultResponse(Boolean.FALSE));
        }
        catch(Exception e) {
            log.error("Friend API | updateFriend() Fail: 에러 발생 => {}", e.getMessage());
            servletResp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(servletResp.getOutputStream(), new FriendResultResponse(Boolean.FALSE));
        }
    }

    @ApiOperation(value = "친구 삭제")
    @ApiResponses({@ApiResponse(code = 200, message = "삭제 완료"), @ApiResponse(code = 400, message = "삭제 실패")})
    @DeleteMapping
    public void deleteFriend(@RequestBody @Valid DeleteFriendRequest request,
                             HttpServletResponse servletResp) throws IOException {

        servletResp.setContentType(APPLICATION_JSON_VALUE);

        try {
            Friend friend = friendService.findById(request.getFriendId());

            if(friendService.deleteFriend(friend)) {
                log.info("Friend API | deleteFriend() Success: 친구 삭제 완료");
                objectMapper.writeValue(servletResp.getOutputStream(), new FriendResultResponse(Boolean.TRUE));
            }
            else {
                log.warn("Friend API | deleteFriend() Fail: 친구 삭제 실패");
                servletResp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                objectMapper.writeValue(servletResp.getOutputStream(), new FriendResultResponse(Boolean.FALSE));
            }
        }
        catch (Exception e) {
            log.error("Friend API | deleteFriend() Fail: 에러 발생 => {}", e.getMessage());
            servletResp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(servletResp.getOutputStream(), new FriendResultResponse(Boolean.FALSE));
        }
    }
}
