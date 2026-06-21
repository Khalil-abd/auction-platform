package com.auction.auth.exception;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ApiError {
    private String errorCode;
    private String message;
    private int status;
    private LocalDateTime timestamp;
    private String path;
    private List<String> details;
}
