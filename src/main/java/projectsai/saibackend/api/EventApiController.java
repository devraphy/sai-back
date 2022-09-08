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

import projectsai.saibackend.dto.event.requestDto.AddEventRequest;
import projectsai.saibackend.dto.event.requestDto.DeleteEventRequest;
import projectsai.saibackend.dto.event.requestDto.UpdateEventRequest;
import projectsai.saibackend.service.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;


@RequestMapping("/api/event")
@RestController
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Event API", description = "이벤트 관련 CRUD 기능을 제공합니다.")
@ApiResponses({@ApiResponse(responseCode = "500", description = "에러 발생"), @ApiResponse(responseCode = "401", description = "토큰 검증 실패")})
public class EventApiController {

    private final EventService eventService;


    /* =================== GET MAPPING =================== */

    @GetMapping
    @Operation(summary = "전체 이벤트 검색", description = "사용자 소유의 모든 이벤트 검색")
    @ApiResponse(responseCode = "200", description = "검색 성공")
    @Parameters({@Parameter(name = "access_token", description = "로그인 시 발행되는 쿠키를 사용합니다.", in = ParameterIn.COOKIE),
            @Parameter(name = "refresh_token", description = "로그인 시 발행되는 쿠키를 사용합니다.", in = ParameterIn.COOKIE)})
    public void searchEvents(HttpServletRequest servletReq, HttpServletResponse servletResp) throws IOException {

        eventService.searchEventApi(servletReq, servletResp);

    }


    /* =================== POST MAPPING =================== */

    @PostMapping
    @Operation(summary = "이벤트 등록")
    @ApiResponse(responseCode = "200", description = "이벤트 등록 성공")
    @Parameters({@Parameter(name = "access_token", description = "로그인 시 발행되는 쿠키를 사용합니다.", in = ParameterIn.COOKIE),
            @Parameter(name = "refresh_token", description = "로그인 시 발행되는 쿠키를 사용합니다.", in = ParameterIn.COOKIE)})
    public void addEvent(@RequestBody @Valid AddEventRequest requestDTO,
                         HttpServletRequest servletReq, HttpServletResponse servletResp) throws IOException {

        eventService.addEventApi(requestDTO, servletReq, servletResp);

    }

    /* =================== PUT MAPPING =================== */

    @PutMapping
    @Operation(summary = "이벤트 변경")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "변경 완료"), @ApiResponse(responseCode = "400", description = "변경 실패")})
    @Parameters({@Parameter(name = "access_token", description = "로그인 시 발행되는 쿠키를 사용합니다.", in = ParameterIn.COOKIE),
            @Parameter(name = "refresh_token", description = "로그인 시 발행되는 쿠키를 사용합니다.", in = ParameterIn.COOKIE)})
    public void updateEvent(@RequestBody @Valid UpdateEventRequest requestDTO, HttpServletResponse servletResp) throws IOException {

        eventService.updateEventApi(requestDTO, servletResp);

    }

    /* =================== DELETE MAPPING =================== */

    @DeleteMapping
    @Operation(summary = "이벤트 삭제")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "삭제 완료"), @ApiResponse(responseCode = "400", description = "삭제 실패")})
    @Parameters({@Parameter(name = "access_token", description = "로그인 시 발행되는 쿠키를 사용합니다.", in = ParameterIn.COOKIE),
            @Parameter(name = "refresh_token", description = "로그인 시 발행되는 쿠키를 사용합니다.", in = ParameterIn.COOKIE)})
    public void deleteEvent(@RequestBody @Valid DeleteEventRequest requestDTO, HttpServletResponse servletResp) throws IOException {

        eventService.deleteEventApi(requestDTO, servletResp);

    }
}