package com.example.UrbanWasteManager.wasteevent.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class PublicWasteEventResponse {
    private String publicId;
    private String description;
    private Double latitude;
    private Double longitude;
    private String status;
    private LocalDateTime reportedAt;
    private String imageUrl;
    private List<String> mediaTokens;
}