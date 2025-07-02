package com.smartattendance.payload.request;

import lombok.Data;

@Data
public class MarkAttendanceRequest {
    private String image;
    private Long periodId;
}