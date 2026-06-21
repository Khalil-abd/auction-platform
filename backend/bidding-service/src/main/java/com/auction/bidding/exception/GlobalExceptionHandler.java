package com.auction.bidding.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // 1. Handle Domain Business Rule Violations (Bad Bids)
    @ExceptionHandler(InvalidBidException.class)
    public ResponseEntity<ApiError> handleInvalidBid(InvalidBidException ex, HttpServletRequest request) {
        log.warn("Business rule violation: {}", ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, "INVALID_BID_AMOUNT", ex.getMessage(), request);
    }

    // 2. Handle Resource Not Found Mapping
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        log.warn("Resource registration absent: {}", ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND", ex.getMessage(), request);
    }

    // 3. Handle Database Concurrency Conflicts (Pessimistic / Optimistic Race Conditions)
    @ExceptionHandler({PessimisticLockingFailureException.class, ObjectOptimisticLockingFailureException.class})
    public ResponseEntity<ApiError> handleDatabaseConflict(Exception ex, HttpServletRequest request) {
        log.error("High-concurrency data collision detected: {}", ex.getMessage());
        String userFriendlyMessage = "The item is receiving a high volume of bids. Your transaction timed out. Please try again.";
        return buildResponse(HttpStatus.CONFLICT, "DATABASE_CONCURRENCY_CONFLICT", userFriendlyMessage, request);
    }

    // 4. Handle DTO Input Validation Failures (e.g., @NotNull, @Min)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationErrors(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<String> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .toList();
        ApiError error = ApiError.builder()
                .errorCode("VALIDATION_FAILED")
                .message("Input argument verification payload constraints broken.")
                .status(HttpStatus.UNPROCESSABLE_CONTENT.value())
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .details(fieldErrors)
                .build();

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body(error);
    }

    // 5. Global Fallback for Unhandled Internal Server Errors
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneralException(Exception ex, HttpServletRequest request) {
        log.error("Unhandled systemic structural failure encountered", ex);
        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "INTERNAL_SERVER_ERROR",
                "An unexpected error occurred on our server side engine.",
                request
        );
    }

    // Private helper payload mapper
    private ResponseEntity<ApiError> buildResponse(HttpStatus status, String code, String msg, HttpServletRequest request) {
        ApiError error = ApiError.builder()
                .errorCode(code)
                .message(msg)
                .status(status.value())
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(status).body(error);
    }
}