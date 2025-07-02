package com.smartattendance.service;

import com.smartattendance.entity.Attendance;
import com.smartattendance.entity.Period;
import com.smartattendance.entity.User;
import com.smartattendance.payload.response.FaceRecognitionResponse;
import com.smartattendance.repository.AttendanceRepository;
import com.smartattendance.repository.PeriodRepository;
import com.smartattendance.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AttendanceService {

    private final UserRepository userRepository;
    private final PeriodRepository periodRepository;
    private final AttendanceRepository attendanceRepository;
    private final FaceRecognitionService faceRecognitionService;

    public AttendanceService(UserRepository userRepository, PeriodRepository periodRepository, AttendanceRepository attendanceRepository, FaceRecognitionService faceRecognitionService) {
        this.userRepository = userRepository;
        this.periodRepository = periodRepository;
        this.attendanceRepository = attendanceRepository;
        this.faceRecognitionService = faceRecognitionService;
    }

    @Transactional
    public Attendance markAttendance(String username, String imageBase64, Long periodId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));

        Period period = periodRepository.findById(periodId)
                .orElseThrow(() -> new RuntimeException("Period not found with id: " + periodId));

        if (attendanceRepository.existsByUserAndPeriodAndDate(user, period, LocalDate.now())) {
            throw new RuntimeException("Attendance already marked for this period.");
        }

        FaceRecognitionResponse recognitionResponse = faceRecognitionService.recognizeFace(imageBase64);
        if (recognitionResponse == null || !recognitionResponse.isSuccess() || recognitionResponse.getUserId() == null) {
            throw new RuntimeException("Face recognition failed: " + (recognitionResponse != null ? recognitionResponse.getError() : "No response from service."));
        }

        if (!recognitionResponse.getUserId().equals(user.getStudentId().toString())) {
            throw new RuntimeException("Face recognized does not match the logged-in user.");
        }

        Attendance attendance = Attendance.builder()
                .user(user)
                .period(period)
                .date(LocalDate.now())
                .status(Attendance.AttendanceStatus.PRESENT)
                .confidenceScore(recognitionResponse.getConfidence())
                .build();

        return attendanceRepository.save(attendance);
    }

    public List<Attendance> getTodayAttendance(User user) {
        return attendanceRepository.findByUserAndDate(user, LocalDate.now());
    }

    public List<Attendance> getUserAttendanceInRange(User user, LocalDate startDate, LocalDate endDate) {
        return attendanceRepository.findByUserAndDateBetween(user, startDate, endDate);
    }

    public Map<String, Object> getAttendanceStats(User user, LocalDate startDate, LocalDate endDate) {
        List<Attendance> attendanceList = getUserAttendanceInRange(user, startDate, endDate);
        long presentCount = attendanceList.stream().filter(a -> a.getStatus() == Attendance.AttendanceStatus.PRESENT).count();

        Map<String, Object> stats = new HashMap<>();
        stats.put("presentCount", presentCount);
        stats.put("totalRecords", attendanceList.size());
        stats.put("attendanceList", attendanceList);

        return stats;
    }

    /**
     * Returns a list of all periods for today, each with the user's attendance status for that period.
     * If no attendance record exists for a period, status is "NOT_MARKED".
     */
    public List<Map<String, Object>> getTodayPeriodAttendance(User user) {
        List<Period> periods = periodRepository.findAllByOrderByPeriodNumberAsc();
        List<Attendance> attendanceList = attendanceRepository.findByUserAndDate(user, LocalDate.now());
        Map<Long, Attendance> attendanceMap = new HashMap<>();
        for (Attendance att : attendanceList) {
            attendanceMap.put(att.getPeriod().getId(), att);
        }
        List<Map<String, Object>> result = new java.util.ArrayList<>();
        for (Period period : periods) {
            Map<String, Object> periodStatus = new HashMap<>();
            periodStatus.put("periodId", period.getId());
            periodStatus.put("periodNumber", period.getPeriodNumber());
            periodStatus.put("startTime", period.getStartTime());
            periodStatus.put("endTime", period.getEndTime());
            Attendance att = attendanceMap.get(period.getId());
            if (att != null) {
                periodStatus.put("status", att.getStatus().name());
            } else {
                periodStatus.put("status", "NOT_MARKED");
            }
            result.add(periodStatus);
        }
        return result;
    }
}
