package com.smartattendance.repository;

import com.smartattendance.entity.Attendance;
import com.smartattendance.entity.User;
import com.smartattendance.entity.Period;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    boolean existsByUserAndPeriodAndDate(User user, Period period, LocalDate date);
    List<Attendance> findByUserAndDate(User user, LocalDate date);
    List<Attendance> findByUserAndDateBetween(User user, LocalDate startDate, LocalDate endDate);
}
