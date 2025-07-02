package com.smartattendance.controller;

import com.smartattendance.entity.ERole;
import com.smartattendance.entity.Role;
import com.smartattendance.entity.User;
import com.smartattendance.payload.request.LoginRequest;
import com.smartattendance.payload.request.RegisterRequest;
import com.smartattendance.payload.response.JwtResponse;
import com.smartattendance.payload.response.MessageResponse;
import com.smartattendance.repository.RoleRepository;
import com.smartattendance.repository.UserRepository;
import com.smartattendance.security.jwt.JwtUtils;
import com.smartattendance.security.services.UserDetailsImpl;
import com.smartattendance.service.FaceRecognitionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    FaceRecognitionService faceRecognitionService;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                userDetails.getName(), // add name
                userDetails.getStudentId(), // add studentId
                roles));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        // Convert face embedding from List<Double> to String
        String faceEmbeddingString = null;
        if (signUpRequest.getFaceEmbedding() != null && !signUpRequest.getFaceEmbedding().isEmpty()) {
            // Manually construct the string to remove brackets and spaces
            faceEmbeddingString = signUpRequest.getFaceEmbedding().stream()
                                               .map(String::valueOf)
                                               .collect(Collectors.joining(","));
        }

        // Create new user's account
        User user = User.builder()
                .username(signUpRequest.getUsername())
                .email(signUpRequest.getEmail())
                .password(encoder.encode(signUpRequest.getPassword()))
                .name(signUpRequest.getName())
                .studentId(signUpRequest.getStudentId())
                .faceEmbedding(faceEmbeddingString)
                .faceRegistered(faceEmbeddingString != null && !faceEmbeddingString.isEmpty())
                .build();

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);
                        break;
                    default:
                        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);

        // Trigger face reload
        faceRecognitionService.reloadFaces();

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }
}
