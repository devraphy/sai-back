package projectsai.saibackend.api;

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

import javax.validation.Valid;

@RestController @Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberApiController {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/join") // 회원 - 가입
    public JoinMemberResponse joinMember(@RequestBody @Valid JoinMemberRequest request) {
        Member member = new Member(request.getName(), request.getEmail(),
                passwordEncoder.encode(request.getPassword()), Boolean.TRUE, "ROLE_USER");

        if(memberService.signUp(member)) {
            log.info("Member API | joinMember() Success: 회원 가입 성공");
            String token = jwtTokenProvider.createToken(member.getEmail(), member.getRole());
            return new JoinMemberResponse(member.getMemberId(), token, Boolean.TRUE);
        }
        log.warn("Member API | joinMember() Fail: 회원 가입 실패");
        return new JoinMemberResponse(null, null, Boolean.FALSE);
    }

    @PostMapping("/login") // 회원 - 로그인
    public LoginMemberResponse loginMember(@RequestBody @Valid LoginMemberRequest request) {

        if(memberService.loginValidation(request.getEmail().toLowerCase(), request.getPassword())) {
            try {
                if (jwtTokenProvider.validateToken(request.getToken())) {
                    return new LoginMemberResponse(request.getEmail().toLowerCase(), null, Boolean.TRUE);
                }
            }
            catch (ExpiredJwtException e) {
                log.info("Member API | loginMember() Success: 로그인 성공 및 refresh_token 발행");
                String refreshToken = jwtTokenProvider.createToken(request.getEmail(), request.getRole());
                return new LoginMemberResponse(request.getEmail().toLowerCase(), refreshToken, Boolean.TRUE);
            }
        }
        log.warn("Member API | loginMember() Fail: 로그인 실패");
        return new LoginMemberResponse(null, null, Boolean.FALSE);
    }

    @PostMapping("/profile") // 회원 - 정보 조회
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
}
