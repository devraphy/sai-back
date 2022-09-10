package projectsai.saibackend.dto.member.responseDto;

import lombok.Builder;
import lombok.Data;
import projectsai.saibackend.domain.Member;


@Data
@Builder
public class SearchMemberResponse {
    private Long id;
    private String email;
    private String name;
    private String password;
    private Boolean result;

    @Builder
    public SearchMemberResponse(Long id, String email, String name, String password, Boolean result) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.password = password;
        this.result = result;
    }

    public static SearchMemberResponse buildResponse(Member user) {
        SearchMemberResponse response = SearchMemberResponse.builder()
                .id(user.getMemberId())
                .email(user.getEmail())
                .name(user.getName())
                .password(user.getPassword())
                .result(Boolean.TRUE)
                .build();
        return response;
    }
}
