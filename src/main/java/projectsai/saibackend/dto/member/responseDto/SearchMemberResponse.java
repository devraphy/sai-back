package projectsai.saibackend.dto.member.responseDto;

import lombok.Builder;
import lombok.Data;
import org.springframework.security.crypto.password.PasswordEncoder;
import projectsai.saibackend.domain.Member;

import java.time.LocalDate;

@Data @Builder
public class SearchMemberResponse {
    private Long id;
    private String email;
    private String name;
    private String password;
    private LocalDate signUpDate;
    private Boolean result;

    @Builder
    public SearchMemberResponse(Long id, String email, String name, String password, LocalDate signUpDate, Boolean result) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.password = password;
        this.signUpDate = signUpDate;
        this.result = result;
    }

    public static SearchMemberResponse buildResponse(Member member, PasswordEncoder passwordEncoder) {
        SearchMemberResponse response = SearchMemberResponse.builder()
                .id(member.getMemberId())
                .email(member.getEmail())
                .name(member.getName())
                .password(passwordEncoder.encode(member.getPassword()))
                .signUpDate(member.getSignUpDate())
                .result(Boolean.TRUE)
                .build();
        return response;
    }
}
