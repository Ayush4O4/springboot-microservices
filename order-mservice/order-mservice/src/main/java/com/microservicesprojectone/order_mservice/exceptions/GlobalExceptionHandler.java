package com.microservicesprojectone.order_mservice.exceptions;

import com.microservicesprojectone.order_mservice.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.error.Error;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleServiceUnavailableException(Exception ex, HttpServletRequest request){
        ErrorResponse response=ErrorResponse.builder()
                .message(ex.getMessage())
                .url(request.getRequestURI())
                .time(LocalDateTime.now())
                .status(HttpStatus.SERVICE_UNAVAILABLE.value())
                .build();
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }
}
