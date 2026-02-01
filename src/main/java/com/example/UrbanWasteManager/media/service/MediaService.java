package com.example.UrbanWasteManager.media.service;

import com.example.UrbanWasteManager.common.exception.ResourceNotFoundException;
import com.example.UrbanWasteManager.media.entity.Media;
import com.example.UrbanWasteManager.media.repository.MediaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import java.net.MalformedURLException;

@Service
@RequiredArgsConstructor
public class MediaService {

    private final MediaRepository mediaRepository;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @Transactional
    public Media storeFile(MultipartFile file) {
        // ... (existing code) ...
        // Normalize file name
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        
        try {
            // Ensure directory exists
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);

            // Generate unique filename and token
            String token = UUID.randomUUID().toString();
            String fileName = token + "_" + originalFileName;
            Path targetLocation = uploadPath.resolve(fileName);

            // Copy file
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // Save Metadata
            Media media = Media.builder()
                    .token(token)
                    .originalFilename(originalFileName)
                    .contentType(file.getContentType())
                    .storagePath(targetLocation.toString())
                    .build();

            return mediaRepository.save(media);

        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + originalFileName + ". Please try again!", ex);
        }
    }

    @Transactional(readOnly = true)
    public Media getMediaByToken(String token) {
        return mediaRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Media not found with token " + token));
    }

    public Resource loadMediaResource(String token) {
        try {
            Media media = getMediaByToken(token);
            Path filePath = Paths.get(media.getStoragePath()).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                return resource;
            } else {
                throw new ResourceNotFoundException("File not found " + token);
            }
        } catch (MalformedURLException ex) {
            throw new ResourceNotFoundException("File not found " + token, ex);
        }
    }
}
