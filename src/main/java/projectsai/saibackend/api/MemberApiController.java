package projectsai.saibackend.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import projectsai.saibackend.domain.Member;
import projectsai.saibackend.dto.member.requestDto.*;
import projectsai.saibackend.dto.member.responseDto.*;
import projectsai.saibackend.security.jwt.JwtTokenProvider;
import projectsai.saibackend.service.MemberService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController @Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberApiController {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/join") // 회원 - 가입
    public void joinMember(@RequestBody @Valid JoinMemberRequest request, HttpServletRequest servletReq, HttpServletResponse servletResp) throws IOException {
        Member member = new Member(request.getName(), request.getEmail(),
                passwordEncoder.encode(request.getPassword()), Boolean.TRUE, "ROLE_USER");

        servletResp.setContentType(APPLICATION_JSON_VALUE);

        if(memberService.signUp(member)) {
            String accessToken = jwtTokenProvider.createAccessToken(member.getEmail(), member.getRole());
            String refreshToken = jwtTokenProvider.createRefreshToken(member.getEmail());

            Cookie access_cookie = tokenToCookie("access_token", accessToken, servletReq);
            Cookie refresh_cookie = tokenToCookie("refresh_token", refreshToken, servletReq);

            servletResp.addCookie(access_cookie);
            servletResp.addCookie(refresh_cookie);
            servletResp.setHeader("role", member.getRole());

            log.info("Member API | joinMember() Success: 회원 가입 성공");

            objectMapper.writeValue(servletResp.getOutputStream(),
                    new JoinMemberResponse(member.getMemberId(), Boolean.TRUE));
        }
        log.warn("Member API | joinMember() Fail: 회원 가입 실패");
        servletResp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        objectMapper.writeValue(servletResp.getOutputStream(),
                new JoinMemberResponse(null, Boolean.FALSE));
    }

    @PostMapping("/login") // 회원 - 로그인
    public void loginMember(@RequestBody @Valid LoginMemberRequest requestDTO, HttpServletRequest servletReq, HttpServletResponse servletResp) throws IOException{

        Cookie[] cookies = servletReq.getCookies();
        String accessToken = servletReq.getCookies()[0].getValue();
        String refreshToken = servletReq.getCookies()[1].getValue();

        servletResp.setContentType(APPLICATION_JSON_VALUE);

        if(memberService.loginValidation(requestDTO.getEmail().toLowerCase(), requestDTO.getPassword())) {
            try {
                if(jwtTokenProvider.validateToken(accessToken)) {
                    log.info("Member API | loginMember() Success: 로그인 성공");
                    objectMapper.writeValue(servletResp.getOutputStream(),
                            new LoginMemberResponse(requestDTO.getEmail(), Boolean.TRUE));
                }
            }
            catch(ExpiredJwtException e) {

                // 여기서 TryCatch로 refreshToken 유효성 검증 및 예외 처리 해야한다??????

                if(jwtTokenProvider.validateToken(refreshToken)) {
                    log.info("Member API | loginMember() Success: 로그인 성공 및 JWT Token 갱신");
                    accessToken = jwtTokenProvider.createAccessToken(requestDTO.getEmail(), requestDTO.getRole());
                    refreshToken = jwtTokenProvider.createRefreshToken(requestDTO.getEmail());

                    Cookie access_cookie = tokenToCookie("access_token", accessToken, servletReq);
                    Cookie refresh_cookie = tokenToCookie("refresh_token", refreshToken, servletReq);

                    servletResp.addCookie(access_cookie);
                    servletResp.addCookie(refresh_cookie);
                    servletResp.setHeader("role", requestDTO.getRole());
                    servletResp.setStatus(HttpServletResponse.SC_OK);

                    objectMapper.writeValue(servletResp.getOutputStream(),
                            new LoginMemberResponse(requestDTO.getEmail(), Boolean.TRUE));
                }
            }
        }
        log.warn("Member API | loginMember() Fail: 로그인 실패");
        servletResp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        objectMapper.writeValue(servletResp.getOutputStream(),
                new LoginMemberResponse(null, Boolean.FALSE));
    }

    @PostMapping("/profile") // 회원 - 정보 조회
    // 이미 로그인을 한 상태이니까 토큰 값만 확인하자.
    // (프론트에서 role 확인해서 못들어오게 막을 것)
    // ==> 이렇게 하려면 Role을 쿠키에 저장해야 하지 않을까?
    public SearchMemberResponse searchMember(@RequestBody @Valid SearchMemberRequest request) {
        if(memberService.loginValidation(request.getEmail(), request.getPassword())) {
            Member findUser = memberService.findByEmail(request.getEmail());
            log.info("Member API | searchMember() Success: 프로필 접근 성공");
            return SearchMemberResponse.buildResponse(findUser);
        }
        log.warn("Member API | searchMember() Fail: 프로필 접근 실패");
        return new SearchMemberResponse(null, null, null, null, null, Boolean.FALSE);
    }

    @PutMapping("/profile") // 회원 - 정보 수정
    public UpdateMemberResponse updateMember(@RequestBody @Valid UpdateMemberRequest request) {
        boolean result = memberService.updateMember(request.getId(), request.getEmail(), request.getName(), request.getPassword());
        if(result) {
            log.info("Member API | updateMember() Success: 프로필 수정 성공");
            return new UpdateMemberResponse(Boolean.TRUE);
        }
        else {
            log.warn("Member API | updateMember() Fail: 프로필 수정 실패");
            return new UpdateMemberResponse(Boolean.FALSE);
        }
    }

    @DeleteMapping("/profile") // 회원 - 탈퇴
    public DeleteMemberResponse deleteMember(@RequestBody @Valid DeleteMemberRequest request) {
        if(memberService.deleteMember(request.getEmail())) {
            log.info("Member API | deleteMember() Success: 탈퇴 성공");
            return new DeleteMemberResponse(Boolean.TRUE);
        }
        log.warn("Member API | deleteMember() Fail: 탈퇴 실패");
        return new DeleteMemberResponse(Boolean.FALSE);
    }


    private Cookie tokenToCookie(String key, String token, HttpServletRequest request) {
        Cookie cookie = new Cookie(key, token);
        cookie.setPath(request.getContextPath());
        cookie.setMaxAge(7 * 86400);
        return cookie;
    }
}
