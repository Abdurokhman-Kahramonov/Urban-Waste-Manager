package com.example.UrbanWasteManager.driver.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class DriverTaskDetail {
    private String taskId;
    private String description;
    private Double latitude;
    private Double longitude;
    private String status;
    private LocalDateTime reportedAt;
    private List<String> mediaUrls;
    private String reporterNote;
}
