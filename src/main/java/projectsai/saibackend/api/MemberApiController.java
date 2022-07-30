package projectsai.saibackend.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import projectsai.saibackend.domain.Member;
import projectsai.saibackend.dto.member.JoinMemberResponse;
import projectsai.saibackend.dto.member.JoinMemberRequest;
import projectsai.saibackend.service.MemberService;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    @PostMapping("/join") // 회원가입
    public JoinMemberResponse joinMember(@RequestBody @Valid JoinMemberRequest request) {
        Member member = new Member(request.getName(), request.getEmail(), request.getPassword(), request.getSignUpDate());
        Long savedMemberId = memberService.join(member);
        return new JoinMemberResponse(savedMemberId);
    }
}
