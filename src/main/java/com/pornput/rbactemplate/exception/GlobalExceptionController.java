package com.pornput.rbactemplate.exception;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ExceptionResponse> handleAccessDeniedException(AccessDeniedException e, HttpServletRequest request) {
        log.warn("Access denied at [{}]: {}", request.getRequestURI(), e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                ExceptionResponse.builder()
                        .message("Access denied")
                        .error("Forbidden")
                        .path(request.getRequestURI())
                        .status(HttpStatus.FORBIDDEN.value())
                        .build()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleException(Exception e, HttpServletRequest request) {
        log.error("Unexpected error at [{}]: {}", request.getRequestURI(), e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ExceptionResponse.builder()
                        .message("Internal server error")
                        .error("Internal Server Error")
                        .path(request.getRequestURI())
                        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .build()
        );
    }

}
