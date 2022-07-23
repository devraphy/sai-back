package projectsai.saibackend.repository;

import org.assertj.core.api.Assertions;
import org.junit.Before;
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

    public Member createMember(String email, String name) {
        Member member = new Member();
        member.setEmail(email);
        member.setName(name);
        member.setPassword("123456!@#");
        member.setSignUpDate(LocalDate.now());
        return member;
    }

    @Test @Transactional
    public void findById() throws Exception {
       //given
        Member member = createMember("abc@gmail.com", "이근형");

        //when
        Long savedId = memberRepository.save(member);
        Member findMember = memberRepository.findById(savedId);

        //then
        Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());
        Assertions.assertThat(findMember.getName()).isEqualTo(member.getName());
        System.out.println(member.getSignUpDate());
    }

    @Test @Transactional
    public void findByEmail() throws Exception {
        // given
        Member member = createMember("abc@gmail.com", "이근형");

        // when
        Long savedId = memberRepository.save(member);
        String email = member.getEmail();
        Member findMember = memberRepository.findByEmail(email);

        //then
        Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());
        System.out.println(member.getSignUpDate());
    }

    @Test @Transactional
    public void findAllMember() throws  Exception {
        // given
        Member member1 = createMember("abc@gmail.com", "이근형");
        Member member2 = createMember("bcd@gmail.com", "라파엘");

        // when
        Long savedMember1 = memberRepository.save(member1);
        Long savedMember2 = memberRepository.save(member2);
        List<Member> all = memberRepository.findAll();

        for(Member member : all) {
            Assertions.assertThat(member.getEmail()).isIn(member1.getEmail(), member2.getEmail());
        }
    }
}