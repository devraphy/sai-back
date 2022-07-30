package projectsai.saibackend.api;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.web.bind.annotation.*;
import projectsai.saibackend.domain.Member;
import projectsai.saibackend.dto.member.*;
import projectsai.saibackend.service.MemberService;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class MemberApiController {
    // 검증 과정 Service에서 해야하나? 어디에서 하는거지? 여튼 검증과정 리뷰해야함.
    // 로그인 및 검증과정 준우 Repository에 todo-spring 코드 확인할 것

    private final MemberService memberService;

    @PostMapping("/join") // 회원 - 가입
    public JoinMemberResponse joinMember(@RequestBody @Valid JoinMemberRequest request) {
        Member member = new Member(request.getName(), request.getEmail(), request.getPassword(), request.getSignUpDate(), true);
        Long savedMemberId = memberService.join(member);
        return new JoinMemberResponse(savedMemberId);
    }

    @PostMapping("/login") // 회원 - 로그인
    public LoginMemberResponse loginMember(@RequestBody @Valid LoginMemberRequest request) {
        Member findMember = memberService.findByEmail(request.getEmail());
        if(findMember.getPassword().equals(request.getPassword()) && findMember.getVisibility().equals(Boolean.TRUE)) {
            return new LoginMemberResponse(findMember.getId(), findMember.getEmail(), findMember.getName(),
                    findMember.getPassword(), findMember.getSignUpDate(), Boolean.TRUE);
        }
        return new LoginMemberResponse(null, null, null, null, null, Boolean.FALSE);
    }

    @GetMapping("/profile/{id}") // 회원 - 정보읽기
    public SearchMemberResponse searchMember(@PathVariable("id") Long id) {
        Member findMember = memberService.findMember(id);
        return new SearchMemberResponse(findMember.getEmail(), findMember.getName(), findMember.getPassword(), findMember.getSignUpDate());
    }

    @PutMapping("/profile/update") // 회원 - 정보수정
    public UpdateMemberResponse updateMember(@RequestBody @Valid UpdateMemberRequest request) {
        try {
            Member findMember = memberService.findByEmail(request.getEmail());

        } catch(EmptyResultDataAccessException e) {
            memberService.updateMember(request.getId(), request.getName(), request.getEmail(), request.getPassword());
            return new UpdateMemberResponse(Boolean.TRUE);
        }
        return new UpdateMemberResponse(Boolean.FALSE);
    }

    @PutMapping("/profile/resign") // 회원 - 탈퇴
    public DeleteMemberResponse deleteMember(@RequestBody @Valid DeleteMemberRequest request) {
        int i = memberService.deleteMember(request.getId());
        if(i == 1) {
            return new DeleteMemberResponse(Boolean.TRUE);
        }
        return new DeleteMemberResponse(Boolean.FALSE);
    }
}
