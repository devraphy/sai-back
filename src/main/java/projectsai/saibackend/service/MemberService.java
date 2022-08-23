package projectsai.saibackend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import projectsai.saibackend.domain.Member;
import projectsai.saibackend.repository.MemberRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor @Slf4j
public class MemberService {

    @PersistenceContext EntityManager em;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    // 회원 가입
    @Transactional
    public boolean signUp(Member user) {
        try {
            if(emailValidation(user.getEmail())) {
                memberRepository.addMember(user);
                log.info("Member Service | signUp() Success: 저장 성공");
                return true;
            }
            else {
                log.warn("Member Service | signUp() Fail: 저장 실패");
                return false;
            }
        }
        catch(Exception e) {
            log.warn("Member Service | signUp() Fail: 에러 발생 => {}", e.getMessage());
            return false;
        }
    }

    // 전체 회원 검색
    public List<Member> findAll() {
        try {
            List<Member> userList = memberRepository.findAll();
            log.info("Member Service | findAll() Success: 검색 성공");
            return userList;
        }
        catch (EmptyResultDataAccessException e) {
            log.warn("Member Service | findAll() Fail: 검색 결과 없음 => {}", e.getMessage());
            return null;
        }
    }

    // 특정 회원 검색
    public Member findMember(Long id) {
        try {
            Member user = memberRepository.findById(id);
            log.info("Member Service | findMember() Success: 검색 성공");
            return user;
        }
        catch (EmptyResultDataAccessException e) {
            log.warn("Member Service | findMember() Fail: 검색 결과 없음 => {}", e.getMessage());
            return null;
        }
    }

    // email 회원 검색
    public Member findByEmail(String email) {
        try {
            Member user = memberRepository.findByEmail(email);
            log.info("Member Service | findByEmail() Success: 검색 성공");
            return user;
        }
        catch (EmptyResultDataAccessException e) {
            log.warn("Member Service | findByEmail() Fail: 검색 결과 없음 => {}", e.getMessage());
            return null;
        }
    }

    // 회원 가입 - email 중복 검사
    public Boolean emailValidation(String email) {
        try {
            Member findUser = memberRepository.findByEmail(email);
            if(findUser.getVisibility().equals(Boolean.FALSE)) {
                log.warn("Member Service | emailValidation() Fail: 탈퇴 사용자 => {}", email);
                return false;
            }
        }
        catch (EmptyResultDataAccessException e) {
            log.info("Member Service | emailValidation() Success: 신규 이메일 => {}", email);
            return true;
        }
        log.warn("Member Service | emailValidation() Fail: 사용중인 이메일 => {}", email);
        return false;
    }

    // 로그인 - email & password 검증
    public boolean loginValidation(String email, String password) {
        try {
            Member user = memberRepository.findByEmail(email);

            if(user.getVisibility().equals(Boolean.FALSE)) {
                log.warn("Member Service | loginValidation() Fail: 탈퇴 사용자 => {}", email);
                return false;
            }

            else if(user.getEmail().equals(email) && passwordEncoder.matches(password, user.getPassword())) {
                log.info("Member Service | loginValidation() Success: 매칭 성공 => {}", email);
                return true;
            }
        }
        catch(EmptyResultDataAccessException e) {
            log.warn("Member Service | loginValidation() Fail: 존재하지 않는 회원 => {}", e.getMessage());
            return false;
        }
        log.warn("Member Service | loginValidation() Fail: 비밀번호 불일치");
        return false;
    }

    // 회원 정보 수정
    @Transactional
    public boolean updateMember(Long id, String email, String name, String password) {
        try {
            Member user = memberRepository.findByEmail(email);

            if(user.getVisibility().equals(Boolean.FALSE)) {
                log.warn("Member Service | updateMember() Fail: 탈퇴 사용자 => {}", email);
                return false;
            }
            else if(user.getEmail().equals(email) && user.getMemberId().equals(id)) {
                user.updateInfo(name, email, password);
                em.flush();
                em.clear();
                log.info("Member Service | updateMember() Success: 이메일 외 정보 수정 성공");
                return true;
            }
        }
        catch(EmptyResultDataAccessException e) {
            Member findUser = memberRepository.findById(id);
            findUser.updateInfo(name, email, password);
            em.flush();
            em.clear();
            log.info("Member Service | updateMember() Success: 이메일 포함 정보 수정 성공");
            return true;
        }
        log.warn("Member Service | updateMember() Fail: 사용중인 이메일 => {}", email);
        return false;
    }

    // 회원 탈퇴(= visibility 수정)
    @Transactional
    public boolean deleteMember(String email) {
        try {
            Member user = memberRepository.findByEmail(email);
            if(user.getVisibility().equals(Boolean.FALSE)) {
                log.warn("Member Service | deleteMember() Fail: 탈퇴 사용자 => {}", email);
                return false;
            }
            else {
                user.deleteMember();
                log.info("Member Service | deleteMember() Success: 탈퇴 성공 => {}", email);
                em.flush();
                em.clear();
                return true;
            }
        }
        catch(EmptyResultDataAccessException e) {
            log.warn("Member Service | deleteMember() Fail: 존재하지 않는 회원 => {}", e.getMessage());
            return false;
        }
    }
}
