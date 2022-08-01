package projectsai.saibackend.api;

import lombok.RequiredArgsConstructor;
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
        // Id와 password 검증
        if(memberService.loginValidation(request.getEmail(), request.getPassword())) { // TRUE
            Member findMember = memberService.findByEmail(request.getEmail());
            return new LoginMemberResponse(findMember.getId(), findMember.getEmail(), findMember.getName(),
                    findMember.getPassword(), findMember.getSignUpDate(), Boolean.TRUE);
        }

        return new LoginMemberResponse(null, null, null, null, null, Boolean.FALSE);
    }

    @PostMapping("/profile") // 회원 - 정보 조회
    public SearchMemberResponse searchMember(@RequestBody @Valid SearchMemberRequest request) {
        Member findMember = memberService.findByEmail(request.getEmail());
        return new SearchMemberResponse(findMember.getEmail(), findMember.getName(), findMember.getPassword(), findMember.getSignUpDate());
    }

    @PutMapping("/profile/update") // 회원 - 정보 수정
    public UpdateMemberResponse updateMember(@RequestBody @Valid UpdateMemberRequest request) {

        if(memberService.updateValidation(request.getEmail())) {
            // 의문 1 - id 값을 이용해서 회원정보를 update 하는데, id 값은 어디에 저장하지? => 안보이는 태그에 저장하면 된다.
            // 의문 2 - id 값을 가지고 update 하는 것이 안정적 & 안전한 프로세스인가? Id 값은 어떻게 가져올 것인가?
            // => 보통의 웹사이트에서는 회원 정보 접근할때 비밀번호 재입력을 요구하는 방식을 사용하는데,
            //    그 이유가 비밀번호 재입력을 통해서 회원정보를 다시 땡겨오기 위함이라고 생각한다.
            //    비밀번호 재입력 과정을 통해서 email을 이용하여 id 값을 가져오면 된다.
            memberService.updateMember(request.getId(), request.getName(), request.getEmail(), request.getPassword());
            return new UpdateMemberResponse(Boolean.TRUE);
        }
        return new UpdateMemberResponse(Boolean.FALSE);
    }

    @PutMapping("/profile/resign") // 회원 - 탈퇴
    public DeleteMemberResponse deleteMember(@RequestBody @Valid DeleteMemberRequest request) {
        int i = memberService.deleteMember(request.getEmail());
        if(i == 1) {
            return new DeleteMemberResponse(Boolean.TRUE);
        }
        return new DeleteMemberResponse(Boolean.FALSE);
    }
}
