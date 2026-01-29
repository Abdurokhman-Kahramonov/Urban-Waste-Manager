package com.example.UrbanWasteManager.driver.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DriverTaskSummary {
    private String taskId;
    private String addressSummary;
    private String priority;
    private String status;
}
