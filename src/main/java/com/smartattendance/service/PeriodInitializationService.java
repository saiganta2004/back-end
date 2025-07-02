package com.smartattendance.service;

import com.smartattendance.entity.Period;
import com.smartattendance.repository.PeriodRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PeriodInitializationService implements CommandLineRunner {
    
    private final PeriodRepository periodRepository;
    
    @Override
    public void run(String... args) {
        initializePeriods();
    }
    
    private void initializePeriods() {
        if (periodRepository.count() == 0) {
            log.info("Initializing default periods...");
            
            List<Period> periods = List.of(
                    Period.builder().periodNumber(1).startTime(LocalTime.of(9, 30)).endTime(LocalTime.of(10, 20)).active(true).build(),
                    Period.builder().periodNumber(2).startTime(LocalTime.of(10, 20)).endTime(LocalTime.of(11, 10)).active(true).build(),
                    Period.builder().periodNumber(3).startTime(LocalTime.of(11, 10)).endTime(LocalTime.of(12, 0)).active(true).build(),
                    Period.builder().periodNumber(4).startTime(LocalTime.of(12, 0)).endTime(LocalTime.of(12, 50)).active(true).build(),
                    Period.builder().periodNumber(5).startTime(LocalTime.of(13, 40)).endTime(LocalTime.of(14, 30)).active(true).build(),
                    Period.builder().periodNumber(6).startTime(LocalTime.of(14, 30)).endTime(LocalTime.of(15, 20)).active(true).build(),
                    Period.builder().periodNumber(7).startTime(LocalTime.of(15, 20)).endTime(LocalTime.of(16, 10)).active(true).build()
            );
            
            periodRepository.saveAll(periods);
            log.info("Default periods initialized successfully");
        }
    }
}
