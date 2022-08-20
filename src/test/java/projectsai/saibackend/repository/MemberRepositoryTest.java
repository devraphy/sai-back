package projectsai.saibackend.repository;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import projectsai.saibackend.domain.Role;
import projectsai.saibackend.domain.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



@SpringBootTest
@Transactional @Slf4j
class UserRepositoryTest {

    @PersistenceContext EntityManager em;
    @Autowired UserRepository userRepository;
    @Autowired PasswordEncoder passwordEncoder;

    private User user1, user2;
    private Long savedId1, savedId2;

    @BeforeEach
    public void createMember() {

        user1 = new User("이근형","abc@gmail.com", passwordEncoder.encode("abcabc"), 1, new ArrayList<>());
        user2 = new User("곽두팔","twoegiht@gmail.com", passwordEncoder.encode("2828"), 1, new ArrayList<>());

        savedId1 = userRepository.addMember(user1);
        savedId2 = userRepository.addMember(user2);

        em.flush();
        em.clear();
    }

    @Test @DisplayName("User - 회원 저장")
    public void saveMember() throws Exception {
        // given
        user1 = new User("저장테스트","save@gmail.com", passwordEncoder.encode("save"), 1, new ArrayList<>());

        // when
        Long savedMemberId = userRepository.addMember(user1);

        // then
        Assertions.assertThat(savedMemberId).isEqualTo(user1.getUserId());
    }

    @Test @DisplayName("User - 전체 검색")
    public void findAllMember() throws Exception {
        // given

        // when
        List<User> allUser = userRepository.findAll();

        // then
        Assertions.assertThat(allUser.size()).isEqualTo(5);
    }

    @Test @DisplayName("User - ID로 검색")
    public void findById() throws Exception {
        //given

        //when
        User findUser1 = userRepository.findById(savedId1);
        User findUser2 = userRepository.findById(savedId2);

        //then
        Assertions.assertThat(findUser1.getUserId()).isEqualTo(savedId1);
        Assertions.assertThat(findUser2.getUserId()).isEqualTo(savedId2);
    }

    @Test @DisplayName("User - Email로 검색")
    public void findByEmail() throws Exception {
        // given
        String email1 = user1.getEmail();
        String email2 = user2.getEmail();


        // when
        User findUser1 = userRepository.findByEmail(email1);
        User findUser2 = userRepository.findByEmail(email2);

        //then
        Assertions.assertThat(findUser1.getUserId()).isEqualTo(user1.getUserId());
        Assertions.assertThat(findUser2.getUserId()).isEqualTo(user2.getUserId());
    }
}