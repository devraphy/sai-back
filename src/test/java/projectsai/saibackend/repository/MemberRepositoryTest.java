package projectsai.saibackend.repository;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import projectsai.saibackend.domain.Member;

import javax.transaction.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;

    private Member member1;
    private Member member2;

    @BeforeEach
    public void createMember() {
        member1 = new Member("이근형","abc@gamil.com", "abcde", LocalDate.now());
        member2 = new Member("곽두팔","twoegiht@gamil.com", "2828", LocalDate.now());
    }


    @Test @Transactional @DisplayName("회원 - ID 검색")
    public void findById() throws Exception {
       //given
        Long savedId = memberRepository.save(member1);

        //when
        Member findMember = memberRepository.findById(savedId);

        //then
        Assertions.assertThat(findMember.getId()).isEqualTo(member1.getId());
        Assertions.assertThat(findMember.getName()).isEqualTo(member1.getName());
    }

    @Test @Transactional @DisplayName("회원 - Email 검색")
    public void findByEmail() throws Exception {
        // given
        Long savedId = memberRepository.save(member1);
        String email = member1.getEmail();

        // when
        Member findMember = memberRepository.findByEmail(email);

        //then
        Assertions.assertThat(findMember.getId()).isEqualTo(member1.getId());
        System.out.println(member1.getSignUpDate());
    }

    @Test @Transactional @DisplayName("회원 - 모든 회원 검색")
    public void findAllMember() throws  Exception {
        // given
        Long savedMember1 = memberRepository.save(member1);
        Long savedMember2 = memberRepository.save(member1);

        // when
        List<Member> all = memberRepository.findAll();

        for(Member member : all) {
            Assertions.assertThat(member.getEmail()).isIn(member1.getEmail(), member1.getEmail());
        }
    }
}