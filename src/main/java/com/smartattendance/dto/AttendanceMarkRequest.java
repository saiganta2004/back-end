package com.smartattendance.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AttendanceMarkRequest {
    
    @NotBlank(message = "Face image is required")
    private String faceImage; // Base64 encoded image
}
