package projectsai.saibackend.repository;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
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
        member1 = new Member("이근형","abc@gamil.com", "abcde", LocalDate.now(), true);
        member2 = new Member("곽두팔","twoegiht@gmail.com", "2828", LocalDate.now(), true);
        savedId1 = memberRepository.save(member1);
        savedId2 = memberRepository.save(member2);
    }

    @Test @DisplayName("Member - 전체 검색")
    public void findAllMember() throws Exception {
        // given

        // when
        List<Member> all = memberRepository.findAll();

        for(Member member : all) {
            Assertions.assertThat(member.getId()).isIn(member1.getId(), member2.getId());
        }
    }

    @Test @DisplayName("Member - ID로 검색")
    public void findById() throws Exception {
        //given

        //when
        Member findMember1 = memberRepository.findById(savedId1);
        Member findMember2 = memberRepository.findById(savedId2);

        //then
        Assertions.assertThat(findMember1.getId()).isEqualTo(savedId1);
        Assertions.assertThat(findMember2.getId()).isEqualTo(savedId2);
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
        Assertions.assertThat(findMember1.getId()).isEqualTo(member1.getId());
        Assertions.assertThat(findMember2.getId()).isEqualTo(member2.getId());
    }

    @Test @DisplayName("Member - 회원 정보 수정")
    public void updateMember() throws Exception {
       //given

       //when
        int i = memberRepository.updateById(savedId2, "명현만", "powerpunch@gmail.com", member2.getPassword());

        //then
        Assertions.assertThat(i).isEqualTo(1);
    }

    @Test @DisplayName("Member - 회원 삭제") @Rollback(false)
    public void deleteMember() throws Exception {
        // given

        // when
        int i = memberRepository.deleteById(savedId2);
        em.flush();
        em.clear();

        // then
        Assertions.assertThat(1).isEqualTo(1);
        log.info("Visibility => " + em.find(Member.class, savedId2).getVisibility() + " | " + em.find(Member.class, savedId2).getName());
        Assertions.assertThat(em.find(Member.class, savedId2).getVisibility()).isEqualTo(Boolean.FALSE);
    }
}