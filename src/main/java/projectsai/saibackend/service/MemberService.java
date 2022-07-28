package projectsai.saibackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import projectsai.saibackend.domain.Member;
import projectsai.saibackend.repository.MemberRepository;

import java.util.List;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
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
        Member findMember = memberRepository.findByEmail(member.getEmail());
        if(findMember.getEmail().equals(member.getEmail())) {
            throw new IllegalStateException("이미 존재하는 이메일입니다.");
        }
    }

    // 전체 회원 검색
    public List<Member> allMembers() {
        return memberRepository.findAll();
    }

    // 특정 회원 검색
    public Member findMember(Long id) {
        return memberRepository.findById(id);
    }
}
