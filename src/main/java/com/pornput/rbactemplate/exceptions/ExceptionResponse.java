package com.pornput.rbactemplate.exceptions;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.Instant;
import java.util.List;

@Builder
@AllArgsConstructor
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExceptionResponse {
    private String message;
    private Integer status;
    private String error;
    private String path;

    private List<ValidateError> validateError;

    @Builder
    @AllArgsConstructor
    @Getter
    private static class ValidateError {
        private String message;
        private String code;
        private String field;
    }

    @Builder.Default
    private Instant timestamp = Instant.now();

    public static List<ValidateError> buildValidateError(
            MethodArgumentNotValidException e
    ) {
        return e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError ->
                        ValidateError.builder()
                                .field(fieldError.getField())
                                .message(fieldError.getDefaultMessage())
                                .code(fieldError.getCode())
                                .build()
                )
                .toList();
    }


}
