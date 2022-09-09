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
import projectsai.saibackend.dto.member.requestDto.*;
import projectsai.saibackend.service.LoginService;
import projectsai.saibackend.service.MemberService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;


@RequestMapping("/api")
@RestController
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Member API", description = "회원 관련 CRUD 기능을 제공합니다.")
@ApiResponses({@ApiResponse(responseCode = "500", description = "에러 발생"), @ApiResponse(responseCode = "401", description = "토큰 검증 실패")})
public class MemberApiController {

    private final MemberService memberService;
    private final LoginService loginService;

    /* =================== GET MAPPING =================== */

    @GetMapping("/login")
    @Operation(summary = "자동 로그인", description = "refresh_token 검증을 이용한 로그인")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "로그인 성공(토큰 갱신 및 Role 발행)"),
            @ApiResponse(responseCode = "400", description = "잘못된 토큰 사용")})
    @Parameters({@Parameter(name = "access_token", description = "회원가입 시 발행되는 쿠키를 사용합니다.", in = ParameterIn.COOKIE),
            @Parameter(name = "refresh_token", description = "회원가입 시 발행되는 쿠키를 사용합니다.", in = ParameterIn.COOKIE)})
    public void tokenLogin(HttpServletRequest servletReq, HttpServletResponse servletResp) throws IOException {

        loginService.autoLoginApi(servletReq, servletResp);

    }

    @GetMapping("/logout")
    @Operation(summary = "로그아웃", description = "모든 토큰 및 Role 말소")
    @ApiResponse(responseCode = "200", description = "로그아웃 성공")
    @Parameters({@Parameter(name = "access_token", description = "로그인 시 발행되는 쿠키를 사용합니다.", in = ParameterIn.COOKIE),
            @Parameter(name = "refresh_token", description = "로그인 시 발행되는 쿠키를 사용합니다.", in = ParameterIn.COOKIE)})
    public void logoutMember(HttpServletResponse servletResp) throws IOException {

        loginService.logoutApi(servletResp);

    }

    @GetMapping("/profile")
    @Operation(summary = "개인 정보 조회", description = "access_token의 payload(email)를 이용한 회원 정보 검색")
    @ApiResponse(responseCode = "200", description = "회원 정보 출력")
    @Parameters({@Parameter(name = "access_token", description = "로그인 시 발행되는 쿠키를 사용합니다.", in = ParameterIn.COOKIE),
            @Parameter(name = "refresh_token", description = "로그인 시 발행되는 쿠키를 사용합니다.", in = ParameterIn.COOKIE)})
    public void showMember(HttpServletRequest servletReq, HttpServletResponse servletResp) throws IOException {

        memberService.getProfileApi(servletReq, servletResp);

    }

    /* =================== POST MAPPING =================== */

    @PostMapping("/email/validation")
    @Operation(summary = "이메일 중복 검증", description = "회원가입 페이지에서 이메일 중복 검사에서 사용됩니다.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "이메일 사용 가능"),
            @ApiResponse(responseCode = "400", description = "이메일 사용 불가")})
    public void emailValidation(@RequestBody @Valid EmailValidationRequest requestDTO,
                                HttpServletResponse servletResp) throws IOException {

        memberService.emailValidationApi(requestDTO, servletResp);

    }

    @PostMapping("/join") // 회원 가입
    @Operation(summary = "회원 가입")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "회원 가입 완료"),
            @ApiResponse(responseCode = "400", description = "회원 가입 실패")})
    public void joinMember(@RequestBody @Valid JoinMemberRequest requestDTO,
                           HttpServletRequest servletReq, HttpServletResponse servletResp) throws IOException {

        memberService.signUpApi(requestDTO, servletReq, servletResp);

    }

    @PostMapping("/login")
    @Operation(summary = "일반 로그인", description = "ID와 PW 입력을 이용한 로그인")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "로그인 성공(토큰 및 Role 발행)"),
            @ApiResponse(responseCode = "400", description = "로그인 실패")})
    public void basicLogin(@RequestBody @Valid LoginMemberRequest requestDTO,
                           HttpServletRequest servletReq, HttpServletResponse servletResp) throws IOException {

        loginService.basicLoginApi(requestDTO, servletReq, servletResp);

    }

    /* =================== PUT MAPPING =================== */

    @PutMapping("/profile")
    @Operation(summary = "개인 정보 변경", description = "이메일, 비밀번호 변경 가능")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "변경 완료"), @ApiResponse(responseCode = "400", description = "변경 실패")})
    @Parameters({@Parameter(name = "access_token", description = "로그인 시 발행되는 쿠키를 사용합니다.", in = ParameterIn.COOKIE),
            @Parameter(name = "refresh_token", description = "로그인 시 발행되는 쿠키를 사용합니다.", in = ParameterIn.COOKIE)})
    public void updateProfile(@RequestBody @Valid UpdateMemberRequest requestDTO,
                              HttpServletRequest servletReq, HttpServletResponse servletResp) throws IOException {

        memberService.updateProfileApi(requestDTO, servletReq, servletResp);

    }

    /* =================== DELETE MAPPING =================== */

    @DeleteMapping("/profile")
    @Operation(summary = "회원 탈퇴", description = "회원 탈퇴 시 모든 토큰 및 Role 말소")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "탈퇴 완료"), @ApiResponse(responseCode = "400", description = "탈퇴 실패")})
    @Parameters({@Parameter(name = "access_token", description = "로그인 시 발행되는 쿠키를 사용합니다.", in = ParameterIn.COOKIE),
            @Parameter(name = "refresh_token", description = "로그인 시 발행되는 쿠키를 사용합니다.", in = ParameterIn.COOKIE)})
    public void deleteMember(@RequestBody @Valid DeleteMemberRequest requestDTO, HttpServletResponse servletResp) throws IOException {

        memberService.deleteMemberApi(requestDTO, servletResp);

    }
}
