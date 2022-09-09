package projectsai.saibackend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import projectsai.saibackend.domain.Member;
import projectsai.saibackend.dto.member.requestDto.DeleteMemberRequest;
import projectsai.saibackend.dto.member.requestDto.EmailValidationRequest;
import projectsai.saibackend.dto.member.requestDto.JoinMemberRequest;
import projectsai.saibackend.dto.member.requestDto.UpdateMemberRequest;
import projectsai.saibackend.dto.member.responseDto.JoinMemberResponse;
import projectsai.saibackend.dto.member.responseDto.MemberResultResponse;
import projectsai.saibackend.dto.member.responseDto.SearchMemberResponse;
import projectsai.saibackend.repository.MemberRepository;
import projectsai.saibackend.security.jwt.JwtProvider;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    @PersistenceContext
    EntityManager em;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtCookieService jwtCookieService;
    private final JwtProvider jwtProvider;
    private final ObjectMapper objectMapper;

    @Transactional // 회원 가입
    public boolean save(Member member) {
        try {
            memberRepository.addMember(member);
            log.info("Member Service | signUp() Success: 저장 성공");
            return true;
        } catch (Exception e) {
            log.error("Member Service | signUp() Fail: 에러 발생 => {}", e.getMessage());
            return false;
        }
    }

    // 전체 회원 검색
    public List<Member> findAll() {
        try {
            List<Member> memberList = memberRepository.findAll();
            log.info("Member Service | findAll() Success: 검색 성공");
            return memberList;
        } catch (EmptyResultDataAccessException e) {
            log.warn("Member Service | findAll() Fail: 검색 결과 없음 => {}", e.getMessage());
            return null;
        } catch (Exception e) {
            log.error("Member Service | findAll() Fail: 에러 발생  => {}", e.getMessage());
            return null;
        }
    }

    // 특정 회원 검색
    public Member findMember(Long id) {
        try {
            Member member = memberRepository.findById(id);
            log.info("Member Service | findMember() Success: 검색 성공");
            return member;
        } catch (EmptyResultDataAccessException e) {
            log.warn("Member Service | findMember() Fail: 검색 결과 없음 => {}", e.getMessage());
            return null;
        } catch (Exception e) {
            log.error("Member Service | findMember() Fail: 에러 발생 => {}", e.getMessage());
            return null;
        }
    }

    // email 회원 검색
    public Member findByEmail(String email) {
        try {
            Member member = memberRepository.findByEmail(email);
            log.info("Member Service | findByEmail(): 검색 성공");
            return member;
        } catch (EmptyResultDataAccessException e) {
            log.warn("Member Service | findByEmail(): 검색 결과 없음 => {}", e.getMessage());
            return null;
        } catch (Exception e) {
            log.error("Member Service | findByEmail(): 에러 발생 => {}", e.getMessage());
            return null;
        }
    }

    // 회원 가입 - email 중복 검사
    public Boolean emailValidation(String email) {
        try {
            Member member = memberRepository.findByEmail(email);
            if (member.getVisibility().equals(Boolean.FALSE)) {
                log.warn("Member Service | emailValidation() Fail: 탈퇴 사용자 => {}", email);
                return false;
            }
        } catch (EmptyResultDataAccessException e) {
            log.info("Member Service | emailValidation() Success: 신규 이메일 => {}", email);
            return true;
        } catch (Exception e) {
            log.error("Member Service | emailValidation() Fail: 에러 발생 => {}", e.getMessage());
        }
        log.warn("Member Service | emailValidation() Fail: 사용중인 이메일 => {}", email);
        return false;
    }

    // 로그인 - email & password 검증
    public boolean loginValidation(String email, String password) {
        try {
            Member member = memberRepository.findByEmail(email);

            if (member.getVisibility().equals(Boolean.FALSE)) {
                log.warn("Member Service | loginValidation() Fail: 탈퇴 사용자 => {}", email);
                return false;
            } else if (passwordEncoder.matches(password, member.getPassword())) {
                log.info("Member Service | loginValidation() Success: 매칭 성공 => {}", email);
                return true;
            }
        } catch (EmptyResultDataAccessException e) {
            log.warn("Member Service | loginValidation() Fail: 존재하지 않는 회원 => {}", e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("Member Service | loginValidation() Fail: 에러 발생 => {}", e.getMessage());
            return false;
        }
        log.warn("Member Service | loginValidation() Fail: 비밀번호 불일치");
        return false;
    }

    @Transactional // 회원 정보 수정
    public boolean updateMember(Long id, String email, String name, String password) {
        try {
            Member member = memberRepository.findByEmail(email);

            if (member.getVisibility().equals(Boolean.FALSE)) {
                log.warn("Member Service | updateMember() Fail: 탈퇴 사용자 => {}", email);
                return false;
            } else if (member.getEmail().equals(email) && member.getMemberId().equals(id)) {
                member.updateInfo(name, email, password);
                log.info("Member Service | updateMember() Success: 이메일 외 정보 수정 성공");
                em.flush();
                em.clear();
                return true;
            }
        } catch (EmptyResultDataAccessException e) {
            try {
                Member member = memberRepository.findById(id);
                member.updateInfo(name, email, password);
                log.info("Member Service | updateMember() Success: 이메일 포함 정보 수정 성공");
                em.flush();
                em.clear();
                return true;
            } catch (Exception ex) {
                log.error("Member Service | updateMember() Fail: findById() 에러 발생 => {}", e.getMessage());
                return false;
            }
        } catch (Exception e) {
            log.error("Member Service | updateMember() Fail: 에러 발생 => {}", e.getMessage());
            return false;
        }
        log.warn("Member Service | updateMember() Fail: 사용중인 이메일 => {}", email);
        return false;
    }

    @Transactional // 회원 탈퇴(= visibility 수정)
    public boolean deleteMember(String email) {
        try {
            Member member = memberRepository.findByEmail(email);
            if (member.getVisibility().equals(Boolean.FALSE)) {
                log.warn("Member Service | deleteMember() Fail: 탈퇴 사용자 => {}", email);
                return false;
            } else {
                member.deleteMember();
                log.info("Member Service | deleteMember() Success: 탈퇴 성공 => {}", email);
                em.flush();
                em.clear();
                return true;
            }
        } catch (EmptyResultDataAccessException e) {
            log.warn("Member Service | deleteMember() Fail: 존재하지 않는 회원 => {}", e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("Member Service | deleteMember() Fail: 에러 발생 => {}", e.getMessage());
            return false;
        }
    }

    // MemberApi - profile 요청
    public void getProfileApi(HttpServletRequest servletReq, HttpServletResponse servletResp) throws IOException {

        servletResp.setContentType(APPLICATION_JSON_VALUE);

        try {
            String accessToken = jwtCookieService.getAccessToken(servletReq);
            String email = jwtProvider.getUserEmail(accessToken);
            Member member = this.findByEmail(email);

            log.info("Member Service | getProfileApi() Success: 프로필 요청 성공");
            objectMapper.writeValue(servletResp.getOutputStream(), SearchMemberResponse.buildResponse(member));
        } catch (Exception e) {
            log.error("Member Service | getProfileApi() Fail: 에러 발생 => {}", e.getMessage());
            servletResp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(servletResp.getOutputStream(), new MemberResultResponse(Boolean.FALSE));
        }
    }

    // MemberApi - email 중복 검증
    public void emailValidationApi(EmailValidationRequest requestDTO, HttpServletResponse servletResp) throws IOException {

        servletResp.setContentType(APPLICATION_JSON_VALUE);

        if (this.emailValidation(requestDTO.getEmail().toLowerCase())) {
            log.info("Member Service | emailValidationApi() Success: 중복 검증 통과");
            objectMapper.writeValue(servletResp.getOutputStream(), new MemberResultResponse(Boolean.TRUE));
        } else {
            log.warn("Member Service | emailValidationApi() Fail: 중복 검증 실패");
            servletResp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(servletResp.getOutputStream(), new MemberResultResponse(Boolean.FALSE));
        }
    }

    @Transactional // MemberApi - 회원가입
    public void signUpApi(JoinMemberRequest requestDTO, HttpServletRequest servletReq,
                          HttpServletResponse servletResp) throws IOException {

        servletResp.setContentType(APPLICATION_JSON_VALUE);

        Member member = new Member(requestDTO.getName(), requestDTO.getEmail().toLowerCase(),
                passwordEncoder.encode(requestDTO.getPassword()), Boolean.TRUE, "ROLE_USER");

        if (this.save(member)) {
            String email = member.getEmail();
            String role = member.getRole();

            jwtCookieService.setTokenInCookie(email, role, servletReq, servletResp);

            log.info("Member API | signUpApi() Success: 회원 가입 완료");
            objectMapper.writeValue(servletResp.getOutputStream(),
                    new JoinMemberResponse(member.getMemberId(), Boolean.TRUE));
        } else {
            log.warn("Member API | signUpApi() Fail: 회원 가입 실패");
            servletResp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(servletResp.getOutputStream(),
                    new MemberResultResponse(Boolean.FALSE));
        }
    }

    @Transactional // MemberApi - 프로필 수정
    public void updateProfileApi(UpdateMemberRequest requestDTO, HttpServletRequest servletReq, HttpServletResponse servletResp) throws IOException {

        servletResp.setContentType(APPLICATION_JSON_VALUE);

        String accessToken = jwtCookieService.getAccessToken(servletReq);
        String userEmail = jwtProvider.getUserEmail(accessToken);
        Member member = this.findByEmail(userEmail);

        boolean result = this.updateMember(member.getMemberId(), requestDTO.getEmail().toLowerCase(),
                requestDTO.getName(), passwordEncoder.encode(requestDTO.getPassword()));

        if (result) {
            log.info("Member Service | updateProfileApi() Success: 프로필 업데이트 완료");
            jwtCookieService.setTokenInCookie(requestDTO.getEmail(), "ROLE_USER", servletReq, servletResp);
            objectMapper.writeValue(servletResp.getOutputStream(), new MemberResultResponse(Boolean.TRUE));
        } else {
            log.warn("Member Service | updateProfileApi() Fail: 프로필 업데이트 실패");
            servletResp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(servletResp.getOutputStream(), new MemberResultResponse(Boolean.FALSE));
        }
    }

    @Transactional // MemberApi - 회원 탈퇴
    public void deleteMemberApi(DeleteMemberRequest requestDTO, HttpServletResponse servletResp) throws IOException {

        servletResp.setContentType(APPLICATION_JSON_VALUE);

        if (this.deleteMember(requestDTO.getEmail())) {
            log.info("Member API | deleteMemberApi() Success: 탈퇴 완료");
            jwtCookieService.terminateCookieAndRole(servletResp);
            objectMapper.writeValue(servletResp.getOutputStream(), new MemberResultResponse(Boolean.TRUE));
        } else {
            log.warn("Member API | deleteMemberApi() Fail: 탈퇴 실패");
            servletResp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(servletResp.getOutputStream(), new MemberResultResponse(Boolean.FALSE));
        }
    }

}
