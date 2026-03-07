package com.pornput.rbactemplate.exception;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// TODO: Test all Exception
@Slf4j
@RestControllerAdvice
public class GlobalExceptionController {
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ExceptionResponse> handleBadRequestException(BadRequestException e, HttpServletRequest request) {
        log.warn("Bad request at [{}]: {}", request.getRequestURI(), e.getMessage());
        return ResponseEntity.badRequest().body(
                ExceptionResponse.builder()
                        .message(e.getMessage())
                        .error("Bad Request")
                        .path(request.getRequestURI())
                        .status(HttpStatus.BAD_REQUEST.value())
                        .build()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        log.warn("Validation failed at [{}]: {} field error(s)", request.getRequestURI(), e.getBindingResult().getFieldErrorCount());
        return ResponseEntity.badRequest().body(
                ExceptionResponse.builder()
                        .message("Validation Failed")
                        .error("Bad Request")
                        .path(request.getRequestURI())
                        .status(HttpStatus.BAD_REQUEST.value())
                        .validateError(ExceptionResponse.buildValidateError(e))
                        .build()
        );
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ExceptionResponse> handleUnauthorizedException(UnauthorizedException e, HttpServletRequest request) {
        log.warn("Unauthorized access at [{}]: {}", request.getRequestURI(), e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ExceptionResponse.builder()
                        .message(e.getMessage())
                        .error("Unauthorized")
                        .path(request.getRequestURI())
                        .status(HttpStatus.UNAUTHORIZED.value())
                        .build()
        );
    }

}
