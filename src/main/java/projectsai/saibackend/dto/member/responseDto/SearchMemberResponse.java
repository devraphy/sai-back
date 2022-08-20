package projectsai.saibackend.dto.member.responseDto;

import lombok.Builder;
import lombok.Data;
import projectsai.saibackend.domain.Member;

import java.sql.Timestamp;

@Data @Builder
public class SearchMemberResponse {
    private Long id;
    private String email;
    private String name;
    private String password;
    private Timestamp signUpDate;
    private Boolean result;

    @Builder
    public SearchMemberResponse(Long id, String email, String name, String password, Timestamp signUpDate, Boolean result) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.password = password;
        this.signUpDate = signUpDate;
        this.result = result;
    }

    public static SearchMemberResponse buildResponse(Member user) {
        SearchMemberResponse response = SearchMemberResponse.builder()
                .id(user.getMemberId())
                .email(user.getEmail())
                .name(user.getName())
                .password(user.getPassword())
                .signUpDate(user.getSignUpDate())
                .result(Boolean.TRUE)
                .build();
        return response;
    }
}
