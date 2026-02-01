package com.example.UrbanWasteManager.admin.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class AdminEventView {
    private String eventId;
    private String reporterEmail;
    private String assignedDriverName;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime lastUpdatedAt;
    private String internalNote;
    private String description;
    private Double latitude;
    private Double longitude;
    private List<String> mediaTokens;
}
