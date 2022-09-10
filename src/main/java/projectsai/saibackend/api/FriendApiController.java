package projectsai.saibackend.api;

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
import projectsai.saibackend.dto.friend.requestDto.AddFriendRequest;
import projectsai.saibackend.dto.friend.requestDto.DeleteFriendRequest;
import projectsai.saibackend.dto.friend.requestDto.UpdateFriendRequest;
import projectsai.saibackend.service.FriendService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;


@RequestMapping("/api/friend")
@RestController
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Friend API", description = "친구 관련 CRUD 기능을 제공합니다.")
@ApiResponses({@ApiResponse(responseCode = "500", description = "에러 발생"), @ApiResponse(responseCode = "401", description = "토큰 검증 실패")})
public class FriendApiController {

    private final FriendService friendService;


    /* =================== GET MAPPING =================== */

    @GetMapping
    @Operation(summary = "모든 친구 검색", description = "사용자 소유의 모든 친구 검색")
    @ApiResponse(responseCode = "200", description = "검색 완료")
    @Parameters({@Parameter(name = "access_token", description = "로그인 시 발행되는 쿠키를 사용합니다.", in = ParameterIn.COOKIE),
            @Parameter(name = "refresh_token", description = "로그인 시 발행되는 쿠키를 사용합니다.", in = ParameterIn.COOKIE)})
    public void findAll(HttpServletRequest servletReq, HttpServletResponse servletResp) throws IOException {

        friendService.findAllFriendsApi(servletReq, servletResp);

    }

    /* =================== POST MAPPING =================== */

    @PostMapping
    @Operation(summary = "친구 등록")
    @ApiResponse(responseCode = "200", description = "친구 등록 완료")
    @Parameters({@Parameter(name = "access_token", description = "로그인 시 발행되는 쿠키를 사용합니다.", in = ParameterIn.COOKIE),
            @Parameter(name = "refresh_token", description = "로그인 시 발행되는 쿠키를 사용합니다.", in = ParameterIn.COOKIE)})
    public void addFriend(@RequestBody @Valid AddFriendRequest requestDTO, HttpServletRequest servletReq, HttpServletResponse servletResp) {

        friendService.addFriendApi(requestDTO, servletReq, servletResp);

    }

    /* =================== PUT MAPPING =================== */

    @PutMapping
    @Operation(summary = "친구 정보 변경")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "변경 완료"), @ApiResponse(responseCode = "400", description = "변경 실패")})
    @Parameters({@Parameter(name = "access_token", description = "로그인 시 발행되는 쿠키를 사용합니다.", in = ParameterIn.COOKIE),
            @Parameter(name = "refresh_token", description = "로그인 시 발행되는 쿠키를 사용합니다.", in = ParameterIn.COOKIE)})
    public void updateFriend(@RequestBody @Valid UpdateFriendRequest requestDTO, HttpServletResponse servletResp) {

        friendService.updateFriendApi(requestDTO, servletResp);

    }

    /* =================== DELETE MAPPING =================== */

    @DeleteMapping
    @Operation(summary = "친구 삭제")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "삭제 완료"), @ApiResponse(responseCode = "400", description = "삭제 실패")})
    @Parameters({@Parameter(name = "access_token", description = "로그인 시 발행되는 쿠키를 사용합니다.", in = ParameterIn.COOKIE),
            @Parameter(name = "refresh_token", description = "로그인 시 발행되는 쿠키를 사용합니다.", in = ParameterIn.COOKIE)})
    public void deleteFriend(@RequestBody @Valid DeleteFriendRequest requestDTO, HttpServletResponse servletResp) {

        friendService.deleteFriendApi(requestDTO, servletResp);

    }
}
