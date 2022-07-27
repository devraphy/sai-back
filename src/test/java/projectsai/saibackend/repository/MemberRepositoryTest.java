package projectsai.saibackend.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import projectsai.saibackend.domain.Member;

import javax.transaction.Transactional;

import java.time.LocalDate;
import java.util.List;



@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;

    private Member member1, member2;
    private Long savedId1, savedId2;

    @BeforeEach
    public void createMember() {
        member1 = new Member("이근형","abc@gamil.com", "abcde", LocalDate.now());
        member2 = new Member("곽두팔","twoegiht@gamil.com", "2828", LocalDate.now());
        savedId1 = memberRepository.save(member1);
        savedId2 = memberRepository.save(member2);
    }


    @Test @DisplayName("회원 - ID 검색")
    public void findById() throws Exception {
       //given
        //when
        Member findMember1 = memberRepository.findById(savedId1);
        Member findMember2 = memberRepository.findById(savedId2);

        //then
        Assertions.assertThat(findMember1.getId()).isEqualTo(savedId1);
        Assertions.assertThat(findMember2.getId()).isEqualTo(savedId2);
    }

    @Test @DisplayName("회원 - Email 검색")
    public void findByEmail() throws Exception {
        // given
        String email1 = member1.getEmail();
        String email2 = member2.getEmail();

        // when
        Member findMember1 = memberRepository.findByEmail(email1);
        Member findMember2 = memberRepository.findByEmail(email2);

        //then
        Assertions.assertThat(findMember1.getId()).isEqualTo(member1.getId());
        Assertions.assertThat(findMember2.getId()).isEqualTo(member2.getId());
    }

    @Test @DisplayName("회원 - 모든 회원 검색")
    public void findAllMember() throws  Exception {
        // given

        // when
        List<Member> all = memberRepository.findAll();

        for(Member member : all) {
            Assertions.assertThat(member.getId()).isIn(member1.getId(), member2.getId());
        }
    }
}