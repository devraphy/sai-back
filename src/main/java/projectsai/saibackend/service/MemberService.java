package projectsai.saibackend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import projectsai.saibackend.domain.Member;
import projectsai.saibackend.repository.MemberRepository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
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
    public Long join(Member member) {
        memberRepository.save(member);
        log.info("회원 가입 성공 => " + member.getEmail());
        return member.getId();
    }

    // 전체 회원 검색
    public List<Member> findAll() {
        return memberRepository.findAll();
    }

    // 특정 회원 검색
    public Member findMember(Long id) {
        return memberRepository.findById(id);
    }

    // email 회원 검색
    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    // **************  Business Methods

    // 회원 가입 - email 중복 검사
    public Boolean emailValidation(String email) {
        try {
            Member findMember = memberRepository.findByEmail(email);
            // 탈퇴한 사용자의 이메일은 재사용 불가
            if(findMember.getVisibility().equals(Boolean.FALSE)) {
                log.info("탈퇴한 사용자의 이메일 => " + email);
                return false;
            }

        } catch (EmptyResultDataAccessException e){
            return true;
        }
        log.info("이미 존재하는 이메일 => " + email);
        return false;
    }

    // 로그인 - email & password 검증
    public boolean loginValidation(String email, String password) {
        Member findMember;

        try {
            findMember = memberRepository.findByEmail(email);
        }
        catch(EmptyResultDataAccessException e) {
            log.warn("Login Validation Fail: 존재하지 않는 이메일 => " + email);
            return false;
        }

        if(findMember.getVisibility().equals(Boolean.FALSE)) {
            log.warn("Login Validation Fail: 탈퇴한 회원의 이메일 => " + email);
            return false;
        }

        else if(findMember.getEmail().equals(email) && findMember.getPassword().equals(password)) {
            log.info("Login Validation Success: 로그인 성공 => " + email);
            return true;
        }
        log.warn("Login ValidationFail: 비밀번호 불일치");
        return false;
    }

    // 회원 정보 수정
    @Transactional
    public boolean updateMember(Long id, String name, String email, String password) {
        Member findMember = new Member();

        try {
            findMember = memberRepository.findByEmail(email);

            if(findMember.getVisibility().equals(Boolean.FALSE)) {
                log.warn("updateMember Fail: 이미 탈퇴한 회원 => " + email);
                return false;
            }
            else if(findMember.getEmail().equals(email) && findMember.getId().equals(id)) {
                findMember.updateInfo(name, email, password);
                em.flush();
                em.clear();
                log.info("updateMember Success: 수정 성공(1)");
                return true;
            }
        }
        catch(EmptyResultDataAccessException exception) {
            findMember = memberRepository.findById(id);
            findMember.updateInfo(name, email, password);
            em.flush();
            em.clear();
            log.info("updateMember Success: 수정 성공(2)");
            return true;
        }
        log.warn("updateMember Fail: 이미 존재하는 이메일 => " + email);
        return false;
    }

    // 회원 탈퇴(= visibility 수정)
    @Transactional
    public boolean deleteMember(String email) {
        Member findMember = new Member();
        try {
            findMember = memberRepository.findByEmail(email);
            if(findMember.getVisibility().equals(Boolean.FALSE)) {
                log.warn("deleteMember Fail: 이미 탈퇴한 회원 => " + email);
                return false;
            }
            else {
                findMember.deleteMember();
                log.info("deleteMember Success: 탈퇴 성공 => " + email);
                em.flush();
                em.clear();
                return true;
            }
        }
        catch(EmptyResultDataAccessException e) {
            log.warn("deleteMember Fail: 존재하지 않는 회원 => " + email);
            return false;
        }
    }
}
