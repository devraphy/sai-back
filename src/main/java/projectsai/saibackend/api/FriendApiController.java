package projectsai.saibackend.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

import static java.util.stream.Collectors.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequestMapping("/api/friend")
@RestController
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Friend API", description = "친구 관련 CRUD 기능을 제공합니다.")
@ApiResponses({@ApiResponse(responseCode = "500", description = "에러 발생"), @ApiResponse(responseCode = "401", description = "토큰 검증 실패")})
public class FriendApiController {

    private final FriendService friendService;
    private final MemberService memberService;
    private final JwtCookieService jwtCookieService;
    private final JwtProvider jwtProvider;
    private final ObjectMapper objectMapper = new ObjectMapper();


    /* =================== GET MAPPING =================== */

    @GetMapping
    @Operation(summary = "모든 친구 검색", description = "사용자 소유의 모든 친구 검색")
    @ApiResponse(responseCode = "200", description = "검색 완료")
    @Parameters({@Parameter(name = "access_token", description = "로그인 시 발행되는 쿠키를 사용합니다.", in = ParameterIn.COOKIE),
            @Parameter(name = "refresh_token", description = "로그인 시 발행되는 쿠키를 사용합니다.", in = ParameterIn.COOKIE)})
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
        } catch (Exception e) {
            log.error("Friend API | findAll() Fail: 오류 발생 => {}", e.getMessage());
            servletResp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(servletResp.getOutputStream(), new FriendResultResponse(Boolean.FALSE));
        }
    }

    /* =================== POST MAPPING =================== */

    @PostMapping
    @Operation(summary = "친구 등록")
    @ApiResponse(responseCode = "200", description = "친구 등록 완료")
    @Parameters({@Parameter(name = "access_token", description = "로그인 시 발행되는 쿠키를 사용합니다.", in = ParameterIn.COOKIE),
            @Parameter(name = "refresh_token", description = "로그인 시 발행되는 쿠키를 사용합니다.", in = ParameterIn.COOKIE)})
    public void addFriend(@RequestBody @Valid AddFriendRequest requestDTO,
                          HttpServletRequest servletReq, HttpServletResponse servletResp) throws IOException {

        servletResp.setContentType(APPLICATION_JSON_VALUE);

        try {
            String accessToken = jwtCookieService.getAccessToken(servletReq);
            String email = jwtProvider.getUserEmail(accessToken);
            Member owner = memberService.findByEmail(email);

            int score = friendService.setInitialScore(requestDTO.getStatus());
            Friend friend = new Friend(owner, requestDTO.getName(), requestDTO.getType(),
                    requestDTO.getStatus(), score, requestDTO.getMemo());

            if (friendService.addFriend(friend)) {
                log.info("Friend API | addFriend() Success: 친구 등록 완료");
                objectMapper.writeValue(servletResp.getOutputStream(), new FriendResultResponse(Boolean.TRUE));
            }
        } catch (Exception e) {
            log.error("Friend API | addFriend() Fail: 에러 발생 => {}", e.getMessage());
            servletResp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(servletResp.getOutputStream(), new FriendResultResponse(Boolean.FALSE));
        }
    }

    /* =================== PUT MAPPING =================== */

    @PutMapping
    @Operation(summary = "친구 정보 변경")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "변경 완료"), @ApiResponse(responseCode = "400", description = "변경 실패")})
    @Parameters({@Parameter(name = "access_token", description = "로그인 시 발행되는 쿠키를 사용합니다.", in = ParameterIn.COOKIE),
            @Parameter(name = "refresh_token", description = "로그인 시 발행되는 쿠키를 사용합니다.", in = ParameterIn.COOKIE)})
    public void updateFriend(@RequestBody @Valid UpdateFriendRequest requestDTO,
                             HttpServletResponse servletResp) throws IOException {

        servletResp.setContentType(APPLICATION_JSON_VALUE);

        try {
            Integer score = friendService.setInitialScore(requestDTO.getStatus());
            boolean result = friendService.updateFriend(requestDTO.getFriendId(), requestDTO.getName(),
                    requestDTO.getType(), requestDTO.getStatus(), score, requestDTO.getMemo());

            if (result) {
                log.info("Friend API | updateFriend() Success: 친구 업데이트 완료");
                objectMapper.writeValue(servletResp.getOutputStream(), new FriendResultResponse(Boolean.TRUE));
                return;
            }
            log.warn("Friend API | updateFriend() Fail: 친구 업데이트 실패");
            servletResp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(servletResp.getOutputStream(), new FriendResultResponse(Boolean.FALSE));
        } catch (Exception e) {
            log.error("Friend API | updateFriend() Fail: 에러 발생 => {}", e.getMessage());
            servletResp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(servletResp.getOutputStream(), new FriendResultResponse(Boolean.FALSE));
        }
    }

    /* =================== DELETE MAPPING =================== */

    @DeleteMapping
    @Operation(summary = "친구 삭제")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "삭제 완료"), @ApiResponse(responseCode = "400", description = "삭제 실패")})
    @Parameters({@Parameter(name = "access_token", description = "로그인 시 발행되는 쿠키를 사용합니다.", in = ParameterIn.COOKIE),
            @Parameter(name = "refresh_token", description = "로그인 시 발행되는 쿠키를 사용합니다.", in = ParameterIn.COOKIE)})
    public void deleteFriend(@RequestBody @Valid DeleteFriendRequest request,
                             HttpServletResponse servletResp) throws IOException {

        servletResp.setContentType(APPLICATION_JSON_VALUE);

        try {
            Friend friend = friendService.findById(request.getFriendId());

            if (friendService.deleteFriend(friend)) {
                log.info("Friend API | deleteFriend() Success: 친구 삭제 완료");
                objectMapper.writeValue(servletResp.getOutputStream(), new FriendResultResponse(Boolean.TRUE));
            } else {
                log.warn("Friend API | deleteFriend() Fail: 친구 삭제 실패");
                servletResp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                objectMapper.writeValue(servletResp.getOutputStream(), new FriendResultResponse(Boolean.FALSE));
            }
        } catch (Exception e) {
            log.error("Friend API | deleteFriend() Fail: 에러 발생 => {}", e.getMessage());
            servletResp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(servletResp.getOutputStream(), new FriendResultResponse(Boolean.FALSE));
        }
    }
}
