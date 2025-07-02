package com.smartattendance.payload.request;

import jakarta.validation.constraints.*;
import java.util.List;
import java.util.Set;

public class RegisterRequest {
    @NotBlank
    @Size(min = 3, max = 50) // Increase max to 50 or more to allow emails as username
    private String username;

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    private Set<String> role;

    @NotBlank
    @Size(min = 6, max = 40)
    private String password;

    private String name;
    private String studentId;
    private List<Double> faceEmbedding;

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<String> getRole() {
        return this.role;
    }

    public void setRole(Set<String> role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public List<Double> getFaceEmbedding() {
        return faceEmbedding;
    }

    public void setFaceEmbedding(List<Double> faceEmbedding) {
        this.faceEmbedding = faceEmbedding;
    }
}