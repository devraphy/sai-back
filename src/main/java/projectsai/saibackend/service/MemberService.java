package projectsai.saibackend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import projectsai.saibackend.domain.Member;
import projectsai.saibackend.repository.MemberRepository;

import javax.persistence.NoResultException;
import java.util.List;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor @Slf4j
public class MemberService {

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
        }
        // 검색 결과가 없어야(null) 신규 이메일이다.
        catch (EmptyResultDataAccessException e) {
            log.info("신규 이메일입니다.");
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
            if(findMember.getEmail().equals(email) && findMember.getPassword().equals(password)) {
                return true;
            }
        }
        catch(EmptyResultDataAccessException e) {
            return false;
        }
        return false;
    }

    // 회원 정보 수정 - 아이디 변경 시, 중복 검증
    public boolean updateValidation(Long id, String email) {
        try {
            // 사용자가 메일 주소를 변경한다면, 변경한 이메일이 존재하는지 검증
            Member findMember = memberRepository.findByEmail(email);

            if(findMember.getVisibility().equals(Boolean.FALSE)) return false;

            // 만약 사용자가 메일 주소를 변경하지 않았다면, 검색된 대상의 id와 email 값이
            // 매개변수로 들어온 id, email 값과 동일하다.
            // => 이런 로직을 짜는 이유는 update 쿼리에서 애초에 다 update 하기 때문이다.
            if(findMember.getId().equals(id) && findMember.getEmail().equals(email)) {
                return true;
            }
        } // 중복 없는 경우에 오류 발생(검색 결과가 없으니까)
        catch (EmptyResultDataAccessException e) {
            return true;
        }
        return false;
    }

    // 회원 정보 수정
    @Transactional
    public int updateMember(Long id, String name, String email, String password) {
        return memberRepository.updateById(id, name, email, password);
    }

    // 회원 탈퇴
    @Transactional
    public int deleteMember(String email) {
        return memberRepository.deleteByEmail(email);
    }
}
