package projectsai.saibackend.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import projectsai.saibackend.domain.Member;
import projectsai.saibackend.repository.MemberRepository;

import java.util.List;


@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;

    private Member user1, user2;
    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    void createMember() throws Exception {
        user1 = new Member("이근형", "abc@gmail.com", passwordEncoder.encode("abcde"), Boolean.TRUE, "ROLE_USER");
        user2 = new Member("박근형", "def@gmail.com", passwordEncoder.encode("abcde"), Boolean.TRUE, "ROLE_USER");
        memberService.signUp(user1);
        memberService.signUp(user2);
    }

    @Test
    @DisplayName("Member - 회원 가입")
    void signUp() throws Exception {
        // given
        Member newUser = new Member("라파파", "rapapa@gmail.com", passwordEncoder.encode("abcde"), Boolean.TRUE, "ROLE_USER");

        // when
        memberService.signUp(newUser);

        // then
        Assertions.assertEquals(newUser.getMemberId(), memberRepository.findByEmail("rapapa@gmail.com").getMemberId());
    }

    @Test
    @DisplayName("Member - 전체 검색")
    void findAll() throws Exception {
        // given

        // when
        List<Member> allUsers = memberService.findAll();

        // then
        Assertions.assertEquals(5, allUsers.size());
    }

    @Test
    @DisplayName("Member - ID 검색")
    void findMember() throws Exception {
        // given

        // when
        Member findUser1 = memberService.findMember(user1.getMemberId());
        Member findUser2 = memberService.findMember(user2.getMemberId());

        // then
        Assertions.assertEquals(user1.getMemberId(), findUser1.getMemberId());
        Assertions.assertEquals(user2.getMemberId(), findUser2.getMemberId());
    }

    @Test
    @DisplayName("Member - Email 검색")
    void findByEmail() throws Exception {
        // given
        String email1 = user1.getEmail();

        // when
        Member findUser = memberService.findByEmail(email1);

        // then
        Assertions.assertEquals(email1, findUser.getEmail());
    }

    @Test
    @DisplayName("Member - Email 중복 검증")
    void emailValidation() throws Exception {
        // given
        String email = user1.getEmail();

        // when
        Boolean result = memberService.emailValidation(email);

        // then
        Assertions.assertFalse(result);
    }

    @Test
    @DisplayName("Member - 로그인 검증") // 추후에 암호화 적용해야함
    public void loginValidation() throws Exception {
        //given
        String email = user1.getEmail();
        String password = user1.getPassword();

        //when
        boolean result = memberService.loginValidation(email, password);

        //then
        Assertions.assertTrue(result);
    }

    @Test
    @DisplayName("Member - 회원 정보 수정")
    public void updateMember() throws Exception {
        //given

        //when
        memberService.updateMember(user1.getMemberId(), "바꾼이메일", "바꾼이름", "바꾼비밀번호");

        //then
        Assertions.assertEquals("바꾼이메일", memberService.findMember(user1.getMemberId()).getEmail());
    }
}