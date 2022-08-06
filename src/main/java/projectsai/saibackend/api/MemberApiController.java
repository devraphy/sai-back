package projectsai.saibackend.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import projectsai.saibackend.domain.Member;
import projectsai.saibackend.dto.member.requestDto.*;
import projectsai.saibackend.dto.member.responseDto.*;
import projectsai.saibackend.service.MemberService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

    @PersistenceContext EntityManager em;
    private final MemberService memberService;

    @PostMapping("/join") // 회원 - 가입
    public JoinMemberResponse joinMember(@RequestBody @Valid JoinMemberRequest request) {

        if(memberService.emailValidation(request.getEmail())) {
            Member member = new Member(request.getName(), request.getEmail(), request.getPassword(), request.getSignUpDate(), Boolean.TRUE);
            Long savedMemberId = memberService.join(member);
            return new JoinMemberResponse(savedMemberId, Boolean.TRUE);
        }
        return new JoinMemberResponse(null, Boolean.FALSE);
    }

    @PostMapping("/login") // 회원 - 로그인
    public LoginMemberResponse loginMember(@RequestBody @Valid LoginMemberRequest request) {

        if(memberService.loginValidation(request.getEmail(), request.getPassword())) {
            Member findMember = memberService.findByEmail(request.getEmail());
            new LoginMemberResponse(findMember.getId(), findMember.getEmail(), findMember.getName(), findMember.getSignUpDate(), Boolean.TRUE);
        }

        return new LoginMemberResponse(null, null, null, null, Boolean.FALSE);
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

    @PutMapping("/profile/update") // 회원 - 정보 수정
    public UpdateMemberResponse updateMember(@RequestBody @Valid UpdateMemberRequest request) {
        boolean result = memberService.updateMember(request.getId(), request.getName(), request.getEmail(), request.getPassword());
        if(result) return new UpdateMemberResponse(Boolean.TRUE);
        else return new UpdateMemberResponse(Boolean.FALSE);
    }

    @PutMapping("/profile/resign") // 회원 - 탈퇴
    public DeleteMemberResponse deleteMember(@RequestBody @Valid DeleteMemberRequest request) {
        Member findMember = memberService.findByEmail(request.getEmail());
        try {
            em.remove(findMember);
        } catch(Exception e) {
            return new DeleteMemberResponse(Boolean.FALSE);
        }
        return new DeleteMemberResponse(Boolean.TRUE);
    }
}
