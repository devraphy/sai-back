package projectsai.saibackend.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import projectsai.saibackend.domain.Member;
import projectsai.saibackend.dto.member.requestDto.*;
import projectsai.saibackend.dto.member.responseDto.*;
import projectsai.saibackend.service.MemberService;

import javax.validation.Valid;
import java.time.LocalDate;

@RestController @Slf4j
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    @PostMapping("/join") // 회원 - 가입
    public JoinMemberResponse joinMember(@RequestBody @Valid JoinMemberRequest request) {
        Member member = new Member(request.getName(), request.getEmail(),
                request.getPassword(), LocalDate.now(), Boolean.TRUE);

        if(memberService.signUp(member)) {
            log.info("joinMember() Success: 회원 가입 성공");
            return new JoinMemberResponse(member.getId(), Boolean.TRUE);
        }
        log.warn("joinMember() Fail: 회원 가입 실패");
        return new JoinMemberResponse(null, Boolean.FALSE);
    }

    @PostMapping("/login") // 회원 - 로그인
    public LoginMemberResponse loginMember(@RequestBody @Valid LoginMemberRequest request) {
        if(memberService.loginValidation(request.getEmail(), request.getPassword())) {
            log.info("loginMember() Success: 로그인 성공");
            return new LoginMemberResponse(request.getEmail(), Boolean.TRUE);
        }
        log.warn("loginMember() Fail: 로그인 실패");
        return new LoginMemberResponse(null, Boolean.FALSE);
    }

    @PostMapping("/profile") // 회원 - 정보 조회
    public SearchMemberResponse searchMember(@RequestBody @Valid SearchMemberRequest request) {
        if(memberService.loginValidation(request.getEmail(), request.getPassword())) {
            Member findMember = memberService.findByEmail(request.getEmail());
            log.info("searchMember() Success: 프로필 접근 성공");
            return new SearchMemberResponse(findMember.getId(), findMember.getEmail(),
                    findMember.getName(), findMember.getPassword(), findMember.getSignUpDate(), Boolean.TRUE);
        }
        log.warn("searchMember() Fail: 프로필 접근 실패");
        return new SearchMemberResponse(null, null, null, null, null, Boolean.FALSE);
    }

    @PutMapping("/profile") // 회원 - 정보 수정
    public UpdateMemberResponse updateMember(@RequestBody @Valid UpdateMemberRequest request) {
        boolean result = memberService.updateMember(request.getId(), request.getName(), request.getEmail(), request.getPassword());
        if(result) {
            log.info("updateMember() Success: 프로필 수정 성공");
            return new UpdateMemberResponse(Boolean.TRUE);
        }
        else {
            log.warn("updateMember() Fail: 프로필 수정 실패");
            return new UpdateMemberResponse(Boolean.FALSE);
        }
    }

    @DeleteMapping("/profile") // 회원 - 탈퇴
    public DeleteMemberResponse deleteMember(@RequestBody @Valid DeleteMemberRequest request) {
        if(memberService.deleteMember(request.getEmail())) {
            log.info("deleteMember() Success: 탈퇴 성공");
            return new DeleteMemberResponse(Boolean.TRUE);
        }
        log.warn("deleteMember() Fail: 탈퇴 실패");
        return new DeleteMemberResponse(Boolean.FALSE);
    }
}
