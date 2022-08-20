package projectsai.saibackend.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import projectsai.saibackend.domain.User;
import projectsai.saibackend.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;


@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;

    private User user1, user2;

    @BeforeEach
    void createMember() throws Exception {
        user1 = new User("이근형", "abc@gmail.com", "abcdefg", 1);
        user2 = new User("박근형", "def@gmail.com", "abcdefg", 1);
        userService.signUp(user1);
        userService.signUp(user2);
    }

    @Test @DisplayName("User - 회원 가입")
    void signUp() throws Exception {
        // given
        User newUser = new User("라파파", "rapapa@gmail.com", "abcdefg", 1);

        // when
        userService.signUp(newUser);

        // then
        Assertions.assertEquals(newUser.getUserId(), userRepository.findByEmail("rapapa@gmail.com").getUserId());
    }

    @Test @DisplayName("User - 전체 검색")
    void findAll() throws Exception {
        // given

        // when
        List<User> allUsers = userService.findAll();

        // then
        Assertions.assertEquals(5, allUsers.size());
    }

    @Test @DisplayName("User - ID 검색")
    void findMember() throws Exception {
        // given

        // when
        User findUser1 = userService.findMember(user1.getUserId());
        User findUser2 = userService.findMember(user2.getUserId());

        // then
        Assertions.assertEquals(user1.getUserId(), findUser1.getUserId());
        Assertions.assertEquals(user2.getUserId(), findUser2.getUserId());
    }

    @Test @DisplayName("User - Email 검색")
    void findByEmail() throws Exception {
        // given
        String email1 = user1.getEmail();

        // when
        User findUser = userService.findByEmail(email1);

        // then
        Assertions.assertEquals(email1, findUser.getEmail());
    }

    @Test @DisplayName("User - Email 중복 검증")
    void emailValidation() throws Exception {
        // given
        String email = user1.getEmail();

        // when
        Boolean result = userService.emailValidation(email);

        // then
        Assertions.assertFalse(result);
    }

    @Test @DisplayName("User - 로그인 검증") // 추후에 암호화 적용해야함
    public void loginValidation() throws Exception {
       //given
        String email = user1.getEmail();
        String password = user1.getPassword();

       //when
        boolean result = userService.loginValidation(email, password);

        //then
        Assertions.assertTrue(result);
    }

    @Test @DisplayName("User - 회원 정보 수정")
    public void updateMember() throws Exception {
       //given

       //when
        userService.updateMember(user1.getUserId(), "바꾼이메일", "바꾼이름", "바꾼비밀번호");

       //then
        Assertions.assertEquals("바꾼이메일", userService.findMember(user1.getUserId()).getEmail());
    }
}