package com.quitsmoking.platform.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ErrorResponse {
    @Schema(example = "2024-07-01T00:00:00")
    private LocalDateTime timestamp;
    @Schema(example = "400")
    private int status;
    @Schema(example = "Bad Request")
    private String error;
    @Schema(example = "Validation failed")
    private String message;
    @Schema(example = "Field XYZ is required")
    private String details;
}
