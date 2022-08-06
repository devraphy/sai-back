package projectsai.saibackend.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import projectsai.saibackend.domain.Member;
import projectsai.saibackend.repository.MemberRepository;

import java.time.LocalDate;
import java.util.List;


@SpringBootTest
@Transactional @Slf4j
class MemberServiceTest {

    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;

    private Member member1, member2;
    private Long savedMemberId1, savedMemberId2;

    @BeforeEach
    void createMember() throws Exception {
        member1 = new Member("이근형", "abc@gmail.com", "abcdefg", LocalDate.now(), true);
        member2 = new Member("박근형", "def@gmail.com", "abcdefg", LocalDate.now(), true);
        savedMemberId1 = memberService.join(member1);
        savedMemberId2 = memberService.join(member2);
    }

    @Test @DisplayName("Member - 회원 가입")
    void join() throws Exception {
        // given
        Member newMember = new Member("라파파", "rapapa@gmail.com", "abcdefg", LocalDate.now(), true);

        // when
        Long savedMemberId = memberService.join(newMember);

        // then
        Assertions.assertEquals(newMember, memberRepository.findById(savedMemberId));
    }

    @Test @DisplayName("Member - 전체 검색")
    void findAll() throws Exception {
        // given

        // when
        List<Member> allMembers = memberService.findAll();

        // then
        for(Member member : allMembers) {
            org.assertj.core.api.Assertions.assertThat(member).isIn(member1, member2);
        }
    }

    @Test @DisplayName("Member - ID 검색")
    void findById() throws Exception {
        // given

        // when
        Member findMember1 = memberService.findMember(savedMemberId1);
        Member findMember2 = memberService.findMember(savedMemberId2);

        // then
        Assertions.assertEquals(savedMemberId1, findMember1.getId());
        Assertions.assertEquals(savedMemberId2, findMember2.getId());
    }

    @Test @DisplayName("Member - Email 검색")
    void findByEmail() throws Exception {
        // given
        String email1 = member1.getEmail();

        // when
        Member findMember = memberRepository.findByEmail(email1);

        // then
        Assertions.assertEquals(findMember.getEmail(), email1);
    }

    @Test @DisplayName("Member - 로그인 검증") // 추후에 암호화 적용해야함
    public void loginValidation() throws Exception {
       //given
        String email = member1.getEmail();
        String password = member1.getPassword();

       //when
        boolean result = memberService.loginValidation(email, password);

        //then
        Assertions.assertEquals(true, result);
    }

    @Test @DisplayName("Member - 회원 정보 수정")
    public void updateMember() throws Exception {
       //given

       //when
        memberService.updateMember(savedMemberId1, "바꾼이름", "바꾼이메일", "바꾼비밀번호");

       //then
        Assertions.assertEquals(memberService.findMember(savedMemberId1).getEmail(), "바꾼이메일");
    }
}