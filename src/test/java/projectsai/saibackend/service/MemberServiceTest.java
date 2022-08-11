package projectsai.saibackend.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import projectsai.saibackend.domain.Member;
import projectsai.saibackend.repository.MemberRepository;

import java.time.LocalDate;
import java.util.List;


@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;

    private Member member1, member2;

    @BeforeEach
    void createMember() throws Exception {
        member1 = new Member("이근형", "abc@gmail.com", "abcdefg", LocalDate.now(), true);
        member2 = new Member("박근형", "def@gmail.com", "abcdefg", LocalDate.now(), true);
        memberService.signUp(member1);
        memberService.signUp(member2);
    }

    @Test @DisplayName("Member - 회원 가입")
    void signUp() throws Exception {
        // given
        Member newMember = new Member("라파파", "rapapa@gmail.com", "abcdefg", LocalDate.now(), true);

        // when
        memberService.signUp(newMember);

        // then
        Assertions.assertEquals(newMember.getId(), memberRepository.findByEmail("rapapa@gmail.com").getId());
    }

    @Test @DisplayName("Member - 전체 검색")
    void findAll() throws Exception {
        // given

        // when
        List<Member> allMembers = memberService.findAll();

        // then
        Assertions.assertEquals(5, allMembers.size());
    }

    @Test @DisplayName("Member - ID 검색")
    void findMember() throws Exception {
        // given

        // when
        Member findMember1 = memberService.findMember(member1.getId());
        Member findMember2 = memberService.findMember(member2.getId());

        // then
        Assertions.assertEquals(member1.getId(), findMember1.getId());
        Assertions.assertEquals(member2.getId(), findMember2.getId());
    }

    @Test @DisplayName("Member - Email 검색")
    void findByEmail() throws Exception {
        // given
        String email1 = member1.getEmail();

        // when
        Member findMember = memberService.findByEmail(email1);

        // then
        Assertions.assertEquals(email1, findMember.getEmail());
    }

    @Test @DisplayName("Member - Email 중복 검증")
    void emailValidation() throws Exception {
        // given
        String email = member1.getEmail();

        // when
        Boolean result = memberService.emailValidation(email);

        // then
        Assertions.assertEquals(Boolean.FALSE, result);
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
        memberService.updateMember(member1.getId(), "바꾼이름", "바꾼이메일", "바꾼비밀번호");

       //then
        Assertions.assertEquals("바꾼이메일", memberService.findMember(member1.getId()).getEmail());
    }
}