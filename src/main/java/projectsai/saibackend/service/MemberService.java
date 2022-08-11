package projectsai.saibackend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
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

    // 회원 가입
    @Transactional
    public boolean signUp(Member member) {
        if(emailValidation(member.getEmail())) {
            memberRepository.save(member);
            log.info("signUp() Success: 회원 가입 성공");
            return true;
        }
        log.warn("signUp() Fail: 회원 가입 실패 => emailValidation() 실패 | " + member.getEmail());
        return false;
    }

    // 전체 회원 검색
    public List<Member> findAll() {
        try {
            List<Member> memberList = memberRepository.findAll();
            log.info("findAll() Success: 모든 회원 검색 성공");
            return memberList;
        } catch (Exception e) {
            log.warn("findAll() Fail: 모든 회원 검색 실패 => " + e.getMessage());
            return null;
        }
    }

    // 특정 회원 검색
    public Member findMember(Long id) {
        try {
            Member member = memberRepository.findById(id);
            log.info("findMember() Success: id로 특정 회원 검색 성공");
            return member;
        } catch (EmptyResultDataAccessException e) {
            log.warn("findMember() Fail: id로 특정 회원 검색 실패 => " + e.getMessage());
            return null;
        }
    }

    // email 회원 검색
    public Member findByEmail(String email) {
        try {
            Member member = memberRepository.findByEmail(email);
            log.info("findByEmail() Success: email로 특정 회원 검색 성공");
            return member;
        } catch (EmptyResultDataAccessException e) {
            log.warn("findByEmail() Fail: email로 특정 회원 검색 실패 => " + e.getMessage());
            return null;
        }
    }

    // 회원 가입 - email 중복 검사
    public Boolean emailValidation(String email) {
        try {
            Member findMember = memberRepository.findByEmail(email);
            if(findMember.getVisibility().equals(Boolean.FALSE)) {
                log.warn("emailValidation() Fail: 탈퇴 사용자의 이메일 => " + email);
                return false;
            }
        }
        catch (EmptyResultDataAccessException e) {
            log.info("emailValidation() Success: 신규 이메일=> " + email);
            return true;
        }
        log.warn("emailValidation() Fail: 중복 이메일 => " + email);
        return false;
    }

    // 로그인 - email & password 검증
    public boolean loginValidation(String email, String password) {
        try {
            Member member = memberRepository.findByEmail(email);
            if(member.getVisibility().equals(Boolean.FALSE)) {
                log.warn("loginValidation() Fail: 탈퇴한 회원의 이메일 => " + email);
                return false;
            }

            else if(member.getEmail().equals(email) && member.getPassword().equals(password)) {
                log.info("loginValidation() Success: 로그인 성공 => " + email);
                return true;
            }
        }
        catch(EmptyResultDataAccessException e) {
            log.warn("loginValidation() Fail: 존재하지 않는 이메일 => " + e.getMessage());
            return false;
        }
        log.warn("loginValidation() Fail: 비밀번호 불일치");
        return false;
    }

    // 회원 정보 수정
    @Transactional
    public boolean updateMember(Long id, String name, String email, String password) {
        try {
            Member member = memberRepository.findByEmail(email);

            if(member.getVisibility().equals(Boolean.FALSE)) {
                log.warn("updateMember() Fail: 탈퇴한 회원의 이메일 => " + email);
                return false;
            }
            else if(member.getEmail().equals(email) && member.getId().equals(id)) {
                member.updateInfo(name, email, password);
                em.flush();
                em.clear();
                log.info("updateMember() Success: 이메일 외 정보 수정 성공");
                return true;
            }
        }
        catch(EmptyResultDataAccessException e) {
            Member findMember = memberRepository.findById(id);
            findMember.updateInfo(name, email, password);
            em.flush();
            em.clear();
            log.info("updateMember() Success: 이메일 포함 정보 수정 성공");
            return true;
        }
        log.warn("updateMember() Fail: 중복 이메일 => " + email);
        return false;
    }

    // 회원 탈퇴(= visibility 수정)
    @Transactional
    public boolean deleteMember(String email) {
        try {
            Member member = memberRepository.findByEmail(email);
            if(member.getVisibility().equals(Boolean.FALSE)) {
                log.warn("deleteMember() Fail: 이미 탈퇴한 회원 => " + email);
                return false;
            }
            else {
                member.deleteMember();
                log.info("deleteMember() Success: 탈퇴 성공 => " + email);
                em.flush();
                em.clear();
                return true;
            }
        }
        catch(EmptyResultDataAccessException e) {
            log.warn("deleteMember() Fail: 존재하지 않는 회원 => " + e.getMessage());
            return false;
        }
    }
}
