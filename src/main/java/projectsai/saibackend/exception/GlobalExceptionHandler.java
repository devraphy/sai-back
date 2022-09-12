package projectsai.saibackend.exception;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.webjars.NotFoundException;

import java.io.IOException;


@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // CLIENT
    @ExceptionHandler(NullPointerException.class)
    protected ResponseEntity<ErrorResponse> handleNullPointer(final NullPointerException e) {
        log.error("handleNullPointer() => {}", e.getMessage());
        return ResponseEntity
                .status(ErrorCode.NULL_REQUEST.getStatus())
                .body(new ErrorResponse(ErrorCode.NULL_REQUEST));
    }

    @ExceptionHandler(NotFoundException.class)
    protected ResponseEntity<ErrorResponse> handleNotFound(final NotFoundException e) {
        log.error("handleNotFound() => {}", e.getMessage());
        return ResponseEntity
                .status(ErrorCode.NOT_FOUND.getStatus())
                .body(new ErrorResponse(ErrorCode.NOT_FOUND));
    }

    @ExceptionHandler(JsonParseException.class)
    protected ResponseEntity<ErrorResponse> handleJsonParse(final JsonParseException e) {
        log.error("handleJsonParse() => {}", e.getMessage());
        return ResponseEntity
                .status(ErrorCode.JSON_ERROR.getStatus())
                .body(new ErrorResponse(ErrorCode.JSON_ERROR));
    }

    @ExceptionHandler(InvalidFormatException.class)
    protected ResponseEntity<ErrorResponse> handleInvalidFormat(final InvalidFormatException e) {
        log.error("handleInvalidFormat() => {}", e.getMessage());
        return ResponseEntity
                .status(ErrorCode.INVALID_INPUT_ERROR.getStatus())
                .body(new ErrorResponse(ErrorCode.INVALID_INPUT_ERROR));
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    protected ResponseEntity<ErrorResponse> handleNoHandlerFound(final NoHandlerFoundException e) {
        log.error("handleNoHandlerFound() => {}", e.getMessage());
        return ResponseEntity
                .status(ErrorCode.NO_HANDLER_FOUND.getStatus())
                .body(new ErrorResponse(ErrorCode.NO_HANDLER_FOUND));
    }

    @ExceptionHandler(DuplicateKeyException.class)
    protected ResponseEntity<ErrorResponse> handleDuplicateKey(final DuplicateKeyException e) {
        log.error("handleDuplicateKey() => {}", e.getMessage());
        return ResponseEntity
                .status(ErrorCode.INVALID_INPUT_ERROR.getStatus())
                .body(new ErrorResponse(ErrorCode.INVALID_INPUT_ERROR));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    protected ResponseEntity<ErrorResponse> handleDataIntegrityViolation(final DataIntegrityViolationException e) {
        log.error("handleDataIntegrityViolation() => {}", e.getMessage());
        return ResponseEntity
                .status(ErrorCode.INVALID_INPUT_ERROR.getStatus())
                .body(new ErrorResponse(ErrorCode.INVALID_INPUT_ERROR));
    }


    // SERVER

    @ExceptionHandler(IOException.class)
    protected ResponseEntity<ErrorResponse> handleIO(final IOException e) {
        log.error("handleIO() => {}", e.getMessage());
        return ResponseEntity
                .status(ErrorCode.IO_ERROR.getStatus())
                .body(new ErrorResponse(ErrorCode.IO_ERROR));
    }

    @ExceptionHandler(RuntimeException.class)
    protected ResponseEntity<ErrorResponse> handleRuntime(final RuntimeException e) {
        log.error("handleRuntime() => {}", e.getMessage());
        return ResponseEntity
                .status(ErrorCode.INTERNAL_SERVER_ERROR.getStatus())
                .body(new ErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR));
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleException(final Exception e) {
        log.error("handleException() => {}", e.getMessage());
        return ResponseEntity
                .status(ErrorCode.INTERNAL_SERVER_ERROR.getStatus())
                .body(new ErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR));
    }


}
