package projectsai.saibackend.dto.member;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class SearchMemberResponse {
    String email;
    String name;
    String password;
    LocalDate signUpDate;
}
