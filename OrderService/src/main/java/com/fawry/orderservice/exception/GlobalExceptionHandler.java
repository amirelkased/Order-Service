package com.fawry.orderservice.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.amqp.AmqpConnectException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> handleEntityNotFound(@NotNull EntityNotFoundException exp, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, exp.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(@NotNull MethodArgumentNotValidException exp, HttpServletRequest request) {
        Map<String, String> fieldErrors = new HashMap<>();
        exp.getBindingResult()
                .getFieldErrors()
                .forEach(error -> fieldErrors.put(error.getField(), error.getDefaultMessage()));

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .date(LocalDateTime.now())
                .message("Validation failed for one or more fields.")
                .errors(fieldErrors)
                .path(getRequestPath(request))
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(RestClientException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleRestClientException(@NotNull Exception exp, HttpServletRequest request) {
        String errorMessage = "There is exception from RestClient Connection failed";
        errorMessage = errorMessage.concat(exp.getMessage());
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, errorMessage, request);
    }

    @ExceptionHandler(AmqpConnectException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleAmqpException(@NotNull Exception exp, HttpServletRequest request) {
        String errorMessage = "There is exception from AMQP Connection failed";
        errorMessage = errorMessage.concat(exp.getMessage());
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, errorMessage, request);
    }

    @ExceptionHandler(CouponInvalidException.class)
    public ResponseEntity<ErrorResponse> handleCouponInvalidException(@NotNull CouponInvalidException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(OutOfStockException.class)
    public ResponseEntity<ErrorResponse> handleOutOfStockException(@NotNull OutOfStockException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(ProductServiceException.class)
    public ResponseEntity<ErrorResponse> handleOutOfStockException(@NotNull ProductServiceException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(PaymentFailedException.class)
    public ResponseEntity<ErrorResponse> handlePaymentFailedException(@NotNull PaymentFailedException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ErrorResponse> handlePaymentFailedException(@NotNull HttpClientErrorException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleGenericException(@NotNull Exception exp, HttpServletRequest request) {
        String errorMessage = "An unexpected error occurred. Please try again later.";
        errorMessage = errorMessage.concat(exp.getMessage());
        log.error(exp.getCause().toString());
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, errorMessage, request);
    }

    private @NotNull ResponseEntity<ErrorResponse> buildErrorResponse(@NotNull HttpStatus status, String message, HttpServletRequest request) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(status.value())
                .date(LocalDateTime.now())
                .message(message)
                .path(getRequestPath(request))
                .build();

        return ResponseEntity.status(status).body(errorResponse);
    }

    private String getRequestPath(@NotNull HttpServletRequest request) {
        return request.getPathInfo() != null ? request.getPathInfo() : request.getServletPath();
    }
}

