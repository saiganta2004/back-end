package com.smartattendance.dto;

import lombok.Data;

@Data
public class SignUpRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String imageData; // Base64 encoded image for face registration
}