package com.smartattendance.repository;

import com.smartattendance.entity.ERole;
import com.smartattendance.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> { // <-- CHANGE THIS FROM Integer to Long
    Optional<Role> findByName(ERole name);
}