package projectsai.saibackend.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import projectsai.saibackend.domain.Member;
import projectsai.saibackend.dto.member.requestDto.*;
import projectsai.saibackend.dto.member.responseDto.*;
import projectsai.saibackend.service.MemberService;

import javax.validation.Valid;

@RestController @Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class UserApiController {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/join") // 회원 - 가입
    public JoinMemberResponse joinMember(@RequestBody @Valid JoinMemberRequest request) {
        Member user = Member.buildMember(request, passwordEncoder);

        if(memberService.signUp(user)) {
            log.info("Member API | joinMember() Success: 회원 가입 성공");
            return new JoinMemberResponse(user.getMemberId(), Boolean.TRUE);
        }
        log.warn("Member API | joinMember() Fail: 회원 가입 실패");
        return new JoinMemberResponse(null, Boolean.FALSE);
    }

    @PostMapping("/login") // 회원 - 로그인
    public LoginMemberResponse loginMember(@RequestBody @Valid LoginMemberRequest request) {
        if(memberService.loginValidation(request.getEmail().toLowerCase(), request.getPassword())) {
            log.info("Member API | loginMember() Success: 로그인 성공");
            return new LoginMemberResponse(request.getEmail().toLowerCase(), Boolean.TRUE);
        }
        log.warn("Member API | loginMember() Fail: 로그인 실패");
        return new LoginMemberResponse(null, Boolean.FALSE);
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
