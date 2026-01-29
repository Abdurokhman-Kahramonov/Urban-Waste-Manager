package com.example.UrbanWasteManager.wasteevent.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class PublicWasteEventResponse {
    private String eventPublicId;
    private Double latitude;
    private Double longitude;
    private String status;
    private LocalDateTime createdAt;
}
