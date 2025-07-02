package com.smartattendance.controller;

import com.smartattendance.entity.Attendance;
import com.smartattendance.entity.Period;
import com.smartattendance.entity.User;
import com.smartattendance.payload.request.MarkAttendanceRequest;
import com.smartattendance.payload.response.ApiResponse;
import com.smartattendance.payload.response.MessageResponse;
import com.smartattendance.repository.PeriodRepository;
import com.smartattendance.repository.UserRepository; // Import UserRepository
import com.smartattendance.security.jwt.JwtUtils;
import com.smartattendance.service.AttendanceService;
// We no longer need UserService
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;
    private final UserRepository userRepository; // Use UserRepository instead of UserService
    private final PeriodRepository periodRepository;
    private final JwtUtils jwtUtils;

    @PostMapping("/mark")
    public ResponseEntity<?> markAttendance(@RequestBody MarkAttendanceRequest request, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(new MessageResponse("Error: Unauthorized"));
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();

        // Log the incoming request for debugging
        System.out.println("[DEBUG] Incoming markAttendance request: image=" + request.getImage() + ", periodId=" + request.getPeriodId());

        // Validate request fields
        if (request.getImage() == null || request.getImage().isEmpty()) {
            System.out.println("[ERROR] Missing image in request");
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Missing image in request"));
        }
        if (request.getPeriodId() == null) {
            System.out.println("[ERROR] Missing periodId in request");
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Missing periodId in request"));
        }

        try {
            Attendance newAttendance = attendanceService.markAttendance(username, request.getImage(), request.getPeriodId());
            return ResponseEntity.ok(newAttendance);
        } catch (RuntimeException e) {
            System.out.println("[ERROR] Exception in markAttendance: " + e.getMessage());
            return ResponseEntity.badRequest().body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @GetMapping("/today")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getTodayAttendance(HttpServletRequest request) {
        try {
            User user = getUserFromRequest(request);
            List<Map<String, Object>> periodAttendance = attendanceService.getTodayPeriodAttendance(user);
            return ResponseEntity.ok(ApiResponse.success(periodAttendance));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Endpoint to fetch attendance data for the calendar view.
     * Responds to GET /attendance/calendar
     */
    @GetMapping("/calendar")
    public ResponseEntity<?> getAttendanceCalendar(
            HttpServletRequest request, // Add this parameter
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {

        try {
            // Get the authenticated user from the request
            User user = getUserFromRequest(request);

            LocalDate start = (startDate != null) ? LocalDate.parse(startDate) : LocalDate.now().withDayOfMonth(1);
            LocalDate end = (endDate != null) ? LocalDate.parse(endDate) : LocalDate.now();
            List<Attendance> attendance = attendanceService.getUserAttendanceInRange(user, start, end);
            return ResponseEntity.ok(ApiResponse.success(attendance));
        } catch (RuntimeException e) {
            // If the token is invalid or missing, return an unauthorized error
            return ResponseEntity.status(401).body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Endpoint to fetch attendance statistics.
     * Responds to GET /attendance/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getAttendanceStats(
            HttpServletRequest request, // Add this parameter
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {

        try {
            // Get the authenticated user from the request
            User user = getUserFromRequest(request);

            LocalDate start = (startDate != null) ? LocalDate.parse(startDate) : LocalDate.now().withDayOfMonth(1);
            LocalDate end = (endDate != null) ? LocalDate.parse(endDate) : LocalDate.now();
            Map<String, Object> stats = attendanceService.getAttendanceStats(user, start, end);
            return ResponseEntity.ok(ApiResponse.success(stats));
        } catch (RuntimeException e) {
            // If the token is invalid or missing, return an unauthorized error
            return ResponseEntity.status(401).body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/periods")
    public ResponseEntity<ApiResponse<List<Period>>> getPeriods(HttpServletRequest request) { // Add request parameter
        try {
            // Ensure the user is authenticated before returning the periods
            getUserFromRequest(request);
            List<Period> periods = periodRepository.findAllByOrderByPeriodNumberAsc();
            return ResponseEntity.ok(ApiResponse.success(periods));
        } catch (RuntimeException e) {
            // If the token is invalid or missing, return an unauthorized error
            return ResponseEntity.status(401).body(ApiResponse.error(e.getMessage()));
        }
    }

    private User getUserFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Error: Invalid or missing token");
        }

        String token = authHeader.substring(7);
        String username = jwtUtils.getUserNameFromJwtToken(token);

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Error: User not found"));
    }
}
