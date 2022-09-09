package projectsai.saibackend.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException.Unauthorized;

import java.io.IOException;


@RestControllerAdvice @Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Unauthorized.class)
    protected ResponseEntity<ErrorResponse> handleUnAuthorizedException(final Unauthorized e) {

        log.error("handleUnAuthorizedException() => {}", e.getMessage());
        return ResponseEntity
                .status(ErrorCode.UNAUTHORIZED.getStatus().value())
                .body(new ErrorResponse(ErrorCode.UNAUTHORIZED));
    }

    @ExceptionHandler(IOException.class)
    protected ResponseEntity<ErrorResponse> handleIOException(final IOException e) {
        log.error("handleIOException() => {}", e.getMessage());
        return ResponseEntity
                .status(ErrorCode.INTERNAL_SERVER_ERROR.getStatus().value())
                .body(new ErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR));
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleException(final Exception e) {
        log.error("handleException() => {}", e.getMessage());
        return ResponseEntity
                .status(ErrorCode.INTERNAL_SERVER_ERROR.getStatus().value())
                .body(new ErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR));
    }


}
