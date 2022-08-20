package projectsai.saibackend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import projectsai.saibackend.domain.Member;
import projectsai.saibackend.repository.MemberRepository;

import java.util.stream.Collectors;

@Service @Slf4j
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        try {
            Member findUser = memberRepository.findByEmail(email);
            return new org.springframework.security.core.userdetails.User(findUser.getEmail(),
                    findUser.getPassword(),
                    findUser.getRoles()
                            .stream()
                            .map(o -> new SimpleGrantedAuthority(o.getPosition()))
                            .collect(Collectors.toList()));
        }
        catch (UsernameNotFoundException e) {
            log.error("존재하지 않는 이메일");
            return null;
        }
        catch (Exception e) {
            log.error("에러 발생 => {}", e.getMessage());
            return null;
        }
    }
}
