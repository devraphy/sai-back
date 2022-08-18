package projectsai.saibackend.repository;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import projectsai.saibackend.domain.Member;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.List;



@SpringBootTest
@Transactional @Slf4j
class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;
    @PersistenceContext EntityManager em;

    private Member member1, member2;
    private Long savedId1, savedId2;

    @BeforeEach
    public void createMember() {
        member1 = new Member("이근형","abc@gmail.com", "abcde", LocalDate.now(), 1);
        member2 = new Member("곽두팔","twoegiht@gmail.com", "2828", LocalDate.now(), 1);
        savedId1 = memberRepository.addMember(member1);
        savedId2 = memberRepository.addMember(member2);
        em.flush();
        em.clear();
    }

    @Test @DisplayName("Member - 회원 저장")
    public void saveMember() throws Exception {
        // given
        member1 = new Member("저장테스트","save@gmail.com", "save", LocalDate.now(), 1);

        // when
        Long savedMemberId = memberRepository.addMember(member1);

        // then
        Assertions.assertThat(savedMemberId).isEqualTo(member1.getMemberId  ());
    }

    @Test @DisplayName("Member - 전체 검색")
    public void findAllMember() throws Exception {
        // given

        // when
        List<Member> allMember = memberRepository.findAll();

        // then
        Assertions.assertThat(allMember.size()).isEqualTo(5);
    }

    @Test @DisplayName("Member - ID로 검색")
    public void findById() throws Exception {
        //given

        //when
        Member findMember1 = memberRepository.findById(savedId1);
        Member findMember2 = memberRepository.findById(savedId2);

        //then
        Assertions.assertThat(findMember1.getMemberId()).isEqualTo(savedId1);
        Assertions.assertThat(findMember2.getMemberId()).isEqualTo(savedId2);
    }

    @Test @DisplayName("Member - Email로 검색")
    public void findByEmail() throws Exception {
        // given
        String email1 = member1.getEmail();
        String email2 = member2.getEmail();


        // when
        Member findMember1 = memberRepository.findByEmail(email1);
        Member findMember2 = memberRepository.findByEmail(email2);

        //then
        Assertions.assertThat(findMember1.getMemberId()).isEqualTo(member1.getMemberId());
        Assertions.assertThat(findMember2.getMemberId()).isEqualTo(member2.getMemberId());
    }
}