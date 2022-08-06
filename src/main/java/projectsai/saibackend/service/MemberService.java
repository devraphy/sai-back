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
    public Long join(Member member) {
        memberRepository.save(member);
        return member.getId();
    }

    // 회원 가입 - email 중복 검사
    public Boolean emailValidation(String email) {
        try {
            Member findMember = memberRepository.findByEmail(email);
            // 탈퇴한 사용자의 이메일은 재사용 불가
            if(findMember.getVisibility().equals(Boolean.FALSE)) return false;

        } catch (EmptyResultDataAccessException e){
            return true;
        }
        return false;
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

    // 로그인 - email & password 검증
    public boolean loginValidation(String email, String password) {
        try {
            Member findMember = memberRepository.findByEmail(email);

            if(findMember.getVisibility().equals(Boolean.FALSE)) {
                return false;
            }
            else if(findMember.getEmail().equals(email) && findMember.getPassword().equals(password)) {
                return true;
            }
        }
        catch(EmptyResultDataAccessException e) {
            return false;
        }
        return false;
    }


    // 회원 정보 수정
    @Transactional
    public boolean updateMember(Long id, String name, String email, String password) {
        try {
            Member byEmail = memberRepository.findByEmail(email);
        }
        catch(EmptyResultDataAccessException e) {
            Member findMember = memberRepository.findById(id);
            findMember.updateInfo(name, email, password);
            em.flush();
            em.clear();
            return true;
        }
        return false;
    }
}
