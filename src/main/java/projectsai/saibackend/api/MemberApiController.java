package projectsai.saibackend.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import projectsai.saibackend.domain.User;
import projectsai.saibackend.dto.member.requestDto.*;
import projectsai.saibackend.dto.member.responseDto.*;
import projectsai.saibackend.service.UserService;

import javax.validation.Valid;

@RestController @Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberApiController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/join") // 회원 - 가입
    public JoinMemberResponse joinMember(@RequestBody @Valid JoinMemberRequest request) {
        User user = User.buildMember(request, passwordEncoder);

        if(userService.signUp(user)) {
            log.info("User API | joinMember() Success: 회원 가입 성공");
            return new JoinMemberResponse(user.getUserId(), Boolean.TRUE);
        }
        log.warn("User API | joinMember() Fail: 회원 가입 실패");
        return new JoinMemberResponse(null, Boolean.FALSE);
    }

    @PostMapping("/login") // 회원 - 로그인
    public LoginMemberResponse loginMember(@RequestBody @Valid LoginMemberRequest request) {
        if(userService.loginValidation(request.getEmail().toLowerCase(), request.getPassword())) {
            log.info("User API | loginMember() Success: 로그인 성공");
            return new LoginMemberResponse(request.getEmail().toLowerCase(), Boolean.TRUE);
        }
        log.warn("User API | loginMember() Fail: 로그인 실패");
        return new LoginMemberResponse(null, Boolean.FALSE);
    }

    @PostMapping("/profile") // 회원 - 정보 조회
    public SearchMemberResponse searchMember(@RequestBody @Valid SearchMemberRequest request) {
        if(userService.loginValidation(request.getEmail(), request.getPassword())) {
            User findUser = userService.findByEmail(request.getEmail());
            log.info("User API | searchMember() Success: 프로필 접근 성공");
            return SearchMemberResponse.buildResponse(findUser);
        }
        log.warn("User API | searchMember() Fail: 프로필 접근 실패");
        return new SearchMemberResponse(null, null, null, null, null, Boolean.FALSE);
    }

    @PutMapping("/profile") // 회원 - 정보 수정
    public UpdateMemberResponse updateMember(@RequestBody @Valid UpdateMemberRequest request) {
        boolean result = userService.updateMember(request.getId(), request.getEmail(), request.getName(), request.getPassword());
        if(result) {
            log.info("User API | updateMember() Success: 프로필 수정 성공");
            return new UpdateMemberResponse(Boolean.TRUE);
        }
        else {
            log.warn("User API | updateMember() Fail: 프로필 수정 실패");
            return new UpdateMemberResponse(Boolean.FALSE);
        }
    }

    @DeleteMapping("/profile") // 회원 - 탈퇴
    public DeleteMemberResponse deleteMember(@RequestBody @Valid DeleteMemberRequest request) {
        if(userService.deleteMember(request.getEmail())) {
            log.info("User API | deleteMember() Success: 탈퇴 성공");
            return new DeleteMemberResponse(Boolean.TRUE);
        }
        log.warn("User API | deleteMember() Fail: 탈퇴 실패");
        return new DeleteMemberResponse(Boolean.FALSE);
    }
}
