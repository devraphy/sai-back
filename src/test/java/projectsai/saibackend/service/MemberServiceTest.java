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
    void join() {
        // given
        Member newMember = new Member("라파파", "rapapa@gmail.com", "abcdefg", LocalDate.now(), true);

        // when
        Long savedMemberId = memberService.join(newMember);

        // then
        Assertions.assertEquals(newMember, memberRepository.findById(savedMemberId));
    }

    @Test @DisplayName("Member - 중복 이메일 검증")
    void validateDuplication() {
        // given
        Member newMember = new Member("이근형", "abc@gmail.com", "abcdefg", LocalDate.now(), true);

        // when
        Assertions.assertThrows(IllegalStateException.class, () -> {
            Long savedMemberId = memberService.join(newMember);
        });

        // then
        log.info("중복 이메일 검증이 올바르게 작동합니다.");
    }

    @Test
    void findAll() {
        // given

        // when
        List<Member> allMembers = memberService.findAll();

        // then
        for(Member member : allMembers) {
            org.assertj.core.api.Assertions.assertThat(member).isIn(member1, member2);
        }
    }

    @Test
    void findMember() {
        // given

        // when
        Member findMember1 = memberService.findMember(savedMemberId1);
        Member findMember2 = memberService.findMember(savedMemberId2);

        // then
        Assertions.assertEquals(savedMemberId1, findMember1.getId());
        Assertions.assertEquals(savedMemberId2, findMember2.getId());
    }

    @Test
    public void updateMember() throws Exception {
       //given

       //when
        int i = memberService.updateMember(savedMemberId2, "이태백", "leemountain@gmail.com", "mountain");

        //then
        Assertions.assertEquals(i, 1);
    }
}