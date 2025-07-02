package com.smartattendance.repository;

import com.smartattendance.entity.Period;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PeriodRepository extends JpaRepository<Period, Long> {

    @Query("SELECT p FROM Period p WHERE :now BETWEEN p.startTime AND p.endTime")
    Optional<Period> findActivePeriod(@Param("now") LocalTime now);

    List<Period> findAllByOrderByPeriodNumberAsc();
}
