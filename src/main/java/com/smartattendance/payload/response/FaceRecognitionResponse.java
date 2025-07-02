package com.smartattendance.payload.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FaceRecognitionResponse {
    private boolean success;

    @JsonProperty("user_id")
    private String userId;

    private String error;
    private Double confidence;
    private boolean recognized;
    private String name;
    private String message;
}