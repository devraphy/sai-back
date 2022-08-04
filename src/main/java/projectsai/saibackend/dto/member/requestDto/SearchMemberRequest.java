package projectsai.saibackend.dto.member.requestDto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class SearchMemberRequest {
    @NotNull
    private String email;
    @NotNull
    private String password;
}