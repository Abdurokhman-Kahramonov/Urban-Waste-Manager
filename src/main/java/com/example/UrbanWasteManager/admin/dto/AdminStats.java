package com.example.UrbanWasteManager.admin.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminStats {
    private long totalEvents;
    private long pendingEvents;
    private long completedEvents;
    private long totalDrivers;
}
