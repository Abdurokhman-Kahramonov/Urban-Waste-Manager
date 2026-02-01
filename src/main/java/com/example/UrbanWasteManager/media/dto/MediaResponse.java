package com.example.UrbanWasteManager.media.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class MediaResponse {
    private String token;
    private String originalFilename;
    private String contentType;
    private LocalDateTime uploadedAt;
}
