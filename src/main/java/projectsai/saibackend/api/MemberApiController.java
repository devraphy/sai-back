package projectsai.saibackend.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import projectsai.saibackend.domain.Member;
import projectsai.saibackend.dto.member.requestDto.*;
import projectsai.saibackend.dto.member.responseDto.*;
import projectsai.saibackend.service.MemberService;

import javax.validation.Valid;
import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    @PostMapping("/join") // 회원 - 가입
    public JoinMemberResponse joinMember(@RequestBody @Valid JoinMemberRequest request) {
        Member member = new Member(request.getName(), request.getEmail(),
                request.getPassword(), LocalDate.now(), Boolean.TRUE);

        if(memberService.signUp(member)) {
            return new JoinMemberResponse(member.getId(), Boolean.TRUE);
        }
        return new JoinMemberResponse(null, Boolean.FALSE);
    }

    @PostMapping("/login") // 회원 - 로그인
    public LoginMemberResponse loginMember(@RequestBody @Valid LoginMemberRequest request) {
        if(memberService.loginValidation(request.getEmail(), request.getPassword())) {
            return new LoginMemberResponse(request.getEmail(), Boolean.TRUE);
        }
        return new LoginMemberResponse(null, Boolean.FALSE);
    }

    @PostMapping("/profile") // 회원 - 정보 조회
    public SearchMemberResponse searchMember(@RequestBody @Valid SearchMemberRequest request) {
        if(memberService.loginValidation(request.getEmail(), request.getPassword())) {
            Member findMember = memberService.findByEmail(request.getEmail());
            return new SearchMemberResponse(findMember.getId(), findMember.getEmail(),
                    findMember.getName(), findMember.getPassword(), findMember.getSignUpDate(), Boolean.TRUE);
        }
        return new SearchMemberResponse(null, null, null, null, null, Boolean.FALSE);
    }

    @PutMapping("/profile") // 회원 - 정보 수정
    public UpdateMemberResponse updateMember(@RequestBody @Valid UpdateMemberRequest request) {
        boolean result = memberService.updateMember(request.getId(), request.getName(), request.getEmail(), request.getPassword());
        if(result) return new UpdateMemberResponse(Boolean.TRUE);
        else return new UpdateMemberResponse(Boolean.FALSE);
    }

    @DeleteMapping("/profile") // 회원 - 탈퇴
    public DeleteMemberResponse deleteMember(@RequestBody @Valid DeleteMemberRequest request) {
        if(memberService.deleteMember(request.getEmail())) {
            return new DeleteMemberResponse(Boolean.TRUE);
        }
        return new DeleteMemberResponse(Boolean.FALSE);
    }
}
