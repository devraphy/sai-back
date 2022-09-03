package projectsai.saibackend.repository;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import projectsai.saibackend.domain.Member;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;


@SpringBootTest
@Transactional
@Slf4j
class MemberRepositoryTest {

    @PersistenceContext
    EntityManager em;
    @Autowired
    MemberRepository memberRepository;
    BCryptPasswordEncoder passwordEncoder;

    private Member user1, user2;
    private Long savedId1, savedId2;

    @BeforeEach
    public void createMember() throws Exception {

        user1 = new Member("이근형", "abc@gmail.com",
                passwordEncoder.encode("abcabc"), Boolean.TRUE, "ROLE_USER");
        user2 = new Member("곽두팔", "twoegiht@gmail.com",
                passwordEncoder.encode("2828"), Boolean.TRUE, "ROLE_USER");

        savedId1 = memberRepository.addMember(user1);
        savedId2 = memberRepository.addMember(user2);

        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("Member - 회원 저장")
    public void saveMember() throws Exception {
        // given
        user1 = new Member("저장테스트", "save@gmail.com",
                passwordEncoder.encode("save"), Boolean.TRUE, "ROLE_USER");

        // when
        Long savedMemberId = memberRepository.addMember(user1);

        // then
        Assertions.assertThat(savedMemberId).isEqualTo(user1.getMemberId());
    }

    @Test
    @DisplayName("Member - 전체 검색")
    public void findAllMember() throws Exception {
        // given

        // when
        List<Member> allMember = memberRepository.findAll();

        // then
        Assertions.assertThat(allMember.size()).isEqualTo(5);
    }

    @Test
    @DisplayName("Member - ID로 검색")
    public void findById() throws Exception {
        //given

        //when
        Member findUser1 = memberRepository.findById(savedId1);
        Member findUser2 = memberRepository.findById(savedId2);

        //then
        Assertions.assertThat(findUser1.getMemberId()).isEqualTo(savedId1);
        Assertions.assertThat(findUser2.getMemberId()).isEqualTo(savedId2);
    }

    @Test
    @DisplayName("Member - Email로 검색")
    public void findByEmail() throws Exception {
        // given
        String email1 = user1.getEmail();
        String email2 = user2.getEmail();

        // when
        Member findUser1 = memberRepository.findByEmail(email1);
        Member findUser2 = memberRepository.findByEmail(email2);

        //then
        Assertions.assertThat(findUser1.getMemberId()).isEqualTo(user1.getMemberId());
        Assertions.assertThat(findUser2.getMemberId()).isEqualTo(user2.getMemberId());
    }
}