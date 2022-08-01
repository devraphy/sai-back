package projectsai.saibackend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import projectsai.saibackend.domain.Member;
import projectsai.saibackend.repository.MemberRepository;

import java.util.List;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor @Slf4j
public class MemberService {

    private final MemberRepository memberRepository;

    // 회원 가입
    @Transactional
    public Long join(Member member) {
        validateDuplicate(member);
        memberRepository.save(member);
        return member.getId();
    }

    // 회원 가입 - email 중복 검사
    private void validateDuplicate(Member member) {
        try {
            Member findMember = memberRepository.findByEmail(member.getEmail());
            if(findMember != null) {
                throw new IllegalStateException("이미 존재하는 이메일입니다.");
            }
        }
        catch (EmptyResultDataAccessException e) {
            log.info("해당 이메일은 존재하지 않는 신규 이메일입니다.");
            return;
        }
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
        Member findMember = memberRepository.findByEmail(email);
        if(findMember.getEmail().equals(email) && findMember.getPassword().equals(password)) {
            return true;
        }
        return false;
    }

    // 회원 정보 수정 - 아이디 변경 시, 중복 검증
    public boolean updateValidation(String email) {
        try {
            // 중복 이메일 검증
            Member findMember = memberRepository.findByEmail(email);

        } catch (EmptyResultDataAccessException e) { // 중복 없는 경우에 오류 발생(검색 결과가 없으니까)
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
