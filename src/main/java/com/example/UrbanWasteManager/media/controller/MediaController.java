package com.example.UrbanWasteManager.media.controller;

import com.example.UrbanWasteManager.media.dto.MediaResponse;
import com.example.UrbanWasteManager.media.entity.Media;
import com.example.UrbanWasteManager.media.service.MediaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/media")
@RequiredArgsConstructor
@Tag(name = "Media Management", description = "API for uploading and retrieving media files")
public class MediaController {

    private final MediaService mediaService;

    @Operation(summary = "Upload media file", description = "Uploads a file (image, doc) and returns a token for reference.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File uploaded successfully",
                    content = @Content(schema = @Schema(implementation = MediaResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid file input"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MediaResponse> uploadFile(@RequestParam("file") MultipartFile file) {
        Media media = mediaService.storeFile(file);
        
        MediaResponse response = MediaResponse.builder()
                .token(media.getToken())
                .originalFilename(media.getOriginalFilename())
                .contentType(media.getContentType())
                .uploadedAt(media.getUploadedAt())
                .build();
                
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{token}/content")
    public ResponseEntity<Resource> getMediaContent(@PathVariable String token) {
        Media media = mediaService.getMediaByToken(token);
        Resource resource = mediaService.loadMediaResource(token);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(media.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + media.getOriginalFilename() + "\"")
                .body(resource);
    }
}
