package com.learning.shared.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private String errorCode;
    private String message;
    private Object details;
    private LocalDateTime timestamp;
    private String path;
    private Integer status;

    public static ErrorResponse of(String errorCode, String message, int status) {
        return ErrorResponse.builder()
                .errorCode(errorCode)
                .message(message)
                .status(status)
                .timestamp(LocalDateTime.now())
                .build();
    }
}

