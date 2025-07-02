package com.smartattendance;

import com.smartattendance.entity.ERole;
import com.smartattendance.entity.Role;
import com.smartattendance.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        // This code runs on application startup
        System.out.println("Checking for initial roles...");

        // Check if ROLE_USER exists, if not, create it.
        if (roleRepository.findByName(ERole.ROLE_USER).isEmpty()) {
            roleRepository.save(new Role(ERole.ROLE_USER));
            System.out.println("Created ROLE_USER.");
        }

        // Check if ROLE_ADMIN exists, if not, create it.
        if (roleRepository.findByName(ERole.ROLE_ADMIN).isEmpty()) {
            roleRepository.save(new Role(ERole.ROLE_ADMIN));
            System.out.println("Created ROLE_ADMIN.");
        }
        
        System.out.println("Initial roles check complete.");
    }
}