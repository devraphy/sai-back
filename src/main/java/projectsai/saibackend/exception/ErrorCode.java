package projectsai.saibackend.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // Client
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청"),
    NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 URL"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "토큰 검증 실패"),
    NULL_REQUEST(HttpStatus.BAD_REQUEST, "NULL 요청 오류"),
    JSON_ERROR(HttpStatus.BAD_REQUEST, "JSON Parsing 오류"),
    INVALID_INPUT_ERROR(HttpStatus.BAD_REQUEST, "Input 값 오류"),
    NO_HANDLER_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 API"),

    // Server
    IO_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "IO 오류"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "내부 서버 오류");

    private final HttpStatus status;
    private final String message;

}
