package projectsai.saibackend.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter @AllArgsConstructor
public enum ErrorCode {

    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청"),
    POSTS_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 URL"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "토큰 검증 실패"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "내부 서버 오류");

    private final HttpStatus status;
    private final String message;

}
