//package projectsai.saibackend;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//import projectsai.saibackend.domain.*;
//import projectsai.saibackend.service.MemberService;
//
//import javax.annotation.PostConstruct;
//import javax.persistence.EntityManager;
//
//@Component
//@RequiredArgsConstructor
//public class DummyData {
//
//    private InitService initService;
//
//    @PostConstruct
//    public void init() {
//        initService.dbInit1();
//        initService.dbInit2();
//    }
//
//    @Component
//    @Transactional
//    @RequiredArgsConstructor
//    static class InitService {
//
//        private final EntityManager em;
//        private final MemberService memberService;
//        private PasswordEncoder passwordEncoder;
//
//        public void dbInit1() {
//
//            Member member1 = new Member("Raphael Lee" ,"raphaellee1014@gmali.com", passwordEncoder.encode("abcabc"), Boolean.TRUE, "ROLE_USER");
//            Member member2 = new Member("David Lee" ,"devraphy@gmali.com", passwordEncoder.encode("123123"), Boolean.TRUE, "ROLE_USER");
//            Member member3 = new Member("test1" ,"test@gmail.com", passwordEncoder.encode("abcabc"), Boolean.TRUE, "ROLE_USER");
//            Member member4 = new Member("test2" ,"resign@gmail.com", passwordEncoder.encode("123123"), Boolean.TRUE, "ROLE_USER");
//
//            memberService.signUp(member1);
//            memberService.signUp(member2);
//            memberService.signUp(member3);
//            memberService.signUp(member4);
//        }
//
//        public void dbInit2() {
//        }
//    }
//}
