package com.pornput.rbactemplate.exception;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionController {
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ExceptionResponse> handleBadRequestException(BadRequestException e, HttpServletRequest request) {
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
