package projectsai.saibackend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import projectsai.saibackend.domain.User;
import projectsai.saibackend.repository.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor @Slf4j
public class UserService {

    @PersistenceContext EntityManager em;
    private final UserRepository userRepository;

    // 회원 가입
    @Transactional
    public boolean signUp(User user) {
        try {
            if(emailValidation(user.getEmail())) {
                userRepository.addMember(user);
                log.info("User Service | signUp() Success: 저장 성공");
                return true;
            }
            else {
                log.warn("User Service | signUp() Fail: 저장 실패");
                return false;
            }
        }
        catch(Exception e) {
            log.warn("User Service | signUp() Fail: 에러 발생 => {}", e.getMessage());
            return false;
        }
    }

    // 전체 회원 검색
    public List<User> findAll() {
        try {
            List<User> userList = userRepository.findAll();
            log.info("User Service | findAll() Success: 검색 성공");
            return userList;
        }
        catch (EmptyResultDataAccessException e) {
            log.warn("User Service | findAll() Fail: 검색 결과 없음 => {}", e.getMessage());
            return null;
        }
    }

    // 특정 회원 검색
    public User findMember(Long id) {
        try {
            User user = userRepository.findById(id);
            log.info("User Service | findMember() Success: 검색 성공");
            return user;
        }
        catch (EmptyResultDataAccessException e) {
            log.warn("User Service | findMember() Fail: 검색 결과 없음 => {}", e.getMessage());
            return null;
        }
    }

    // email 회원 검색
    public User findByEmail(String email) {
        try {
            User user = userRepository.findByEmail(email);
            log.info("User Service | findByEmail() Success: 검색 성공");
            return user;
        }
        catch (EmptyResultDataAccessException e) {
            log.warn("User Service | findByEmail() Fail: 검색 결과 없음 => {}", e.getMessage());
            return null;
        }
    }

    // 회원 가입 - email 중복 검사
    public Boolean emailValidation(String email) {
        try {
            User findUser = userRepository.findByEmail(email);
            if(findUser.getVisibility().equals(Boolean.FALSE)) {
                log.warn("User Service | emailValidation() Fail: 탈퇴 사용자 => {}", email);
                return false;
            }
        }
        catch (EmptyResultDataAccessException e) {
            log.info("User Service | emailValidation() Success: 신규 이메일 => {}", email);
            return true;
        }
        log.warn("User Service | emailValidation() Fail: 사용중인 이메일 => {}", email);
        return false;
    }

    // 로그인 - email & password 검증
    public boolean loginValidation(String email, String password) {
        try {
            User user = userRepository.findByEmail(email);
            if(user.getVisibility().equals(Boolean.FALSE)) {
                log.warn("User Service | loginValidation() Fail: 탈퇴 사용자 => {}", email);
                return false;
            }

            else if(user.getEmail().equals(email) && user.getPassword().equals(password)) {
                log.info("User Service | loginValidation() Success: 로그인 성공 => {}", email);
                return true;
            }
        }
        catch(EmptyResultDataAccessException e) {
            log.warn("User Service | loginValidation() Fail: 존재하지 않는 회원 => {}", e.getMessage());
            return false;
        }
        log.warn("User Service | loginValidation() Fail: 비밀번호 불일치");
        return false;
    }

    // 회원 정보 수정
    @Transactional
    public boolean updateMember(Long id, String email, String name, String password) {
        try {
            User user = userRepository.findByEmail(email);

            if(user.getVisibility().equals(Boolean.FALSE)) {
                log.warn("User Service | updateMember() Fail: 탈퇴 사용자 => {}", email);
                return false;
            }
            else if(user.getEmail().equals(email) && user.getUserId().equals(id)) {
                user.updateInfo(name, email, password);
                em.flush();
                em.clear();
                log.info("User Service | updateMember() Success: 이메일 외 정보 수정 성공");
                return true;
            }
        }
        catch(EmptyResultDataAccessException e) {
            User findUser = userRepository.findById(id);
            findUser.updateInfo(name, email, password);
            em.flush();
            em.clear();
            log.info("User Service | updateMember() Success: 이메일 포함 정보 수정 성공");
            return true;
        }
        log.warn("User Service | updateMember() Fail: 사용중인 이메일 => {}", email);
        return false;
    }

    // 회원 탈퇴(= visibility 수정)
    @Transactional
    public boolean deleteMember(String email) {
        try {
            User user = userRepository.findByEmail(email);
            if(user.getVisibility().equals(Boolean.FALSE)) {
                log.warn("User Service | deleteMember() Fail: 탈퇴 사용자 => {}", email);
                return false;
            }
            else {
                user.deleteMember();
                log.info("User Service | deleteMember() Success: 탈퇴 성공 => {}", email);
                em.flush();
                em.clear();
                return true;
            }
        }
        catch(EmptyResultDataAccessException e) {
            log.warn("User Service | deleteMember() Fail: 존재하지 않는 회원 => {}", e.getMessage());
            return false;
        }
    }
}
