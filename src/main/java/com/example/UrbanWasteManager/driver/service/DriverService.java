package com.example.UrbanWasteManager.driver.service;

import com.example.UrbanWasteManager.driver.dto.DriverTaskDetail;
import com.example.UrbanWasteManager.driver.dto.DriverTaskSummary;
import com.example.UrbanWasteManager.driver.entity.DriverAction;
import com.example.UrbanWasteManager.driver.repository.DriverActionRepository;
import com.example.UrbanWasteManager.media.entity.Media;
import com.example.UrbanWasteManager.media.repository.MediaRepository;
import com.example.UrbanWasteManager.media.service.MediaService;
import com.example.UrbanWasteManager.user.entity.User;
import com.example.UrbanWasteManager.user.repository.UserRepository;
import com.example.UrbanWasteManager.wasteevent.entity.WasteEvent;
import com.example.UrbanWasteManager.wasteevent.repository.WasteEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DriverService {

    private final WasteEventRepository wasteEventRepository;
    private final DriverActionRepository driverActionRepository;
    private final UserRepository userRepository;
    private final MediaService mediaService;
    private final MediaRepository mediaRepository;

    private User getCurrentDriver() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Driver not found"));
    }

    @Transactional(readOnly = true)
    public List<DriverTaskSummary> getAssignedTasks() {
        User driver = getCurrentDriver();
        return wasteEventRepository.findAllByAssignedDriverId(driver.getId()).stream()
                .filter(event -> !event.getStatus().equals("COMPLETED") && !event.getStatus().equals("CANCELLED"))
                .map(event -> DriverTaskSummary.builder()
                        .taskId(event.getPublicId())
                        .addressSummary("Lat: " + event.getLatitude() + ", Lng: " + event.getLongitude()) // Placeholder for geocoding
                        .status(event.getStatus())
                        .priority("NORMAL") // Placeholder
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DriverTaskDetail getTaskDetail(String taskId) {
        User driver = getCurrentDriver();
        WasteEvent event = wasteEventRepository.findByPublicId(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (!event.getAssignedDriver().getId().equals(driver.getId())) {
            throw new RuntimeException("Access denied: Task not assigned to this driver");
        }

        List<String> mediaTokens = mediaRepository.findAllByWasteEventId(event.getId()).stream()
                .map(Media::getToken)
                .collect(Collectors.toList());

        return DriverTaskDetail.builder()
                .taskId(event.getPublicId())
                .description(event.getDescription())
                .latitude(event.getLatitude())
                .longitude(event.getLongitude())
                .status(event.getStatus())
                .reportedAt(event.getCreatedAt())
                .mediaUrls(Collections.emptyList())
                .mediaTokens(mediaTokens)
                .reporterNote(event.getDescription())
                .build();
    }

    @Transactional
    public void updateTaskStatus(String taskId, String action, String newStatus) {
        updateTaskStatus(taskId, action, newStatus, null);
    }

    @Transactional
    public void updateTaskStatus(String taskId, String action, String newStatus, String mediaToken) {
        User driver = getCurrentDriver();
        WasteEvent event = wasteEventRepository.findByPublicId(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (!event.getAssignedDriver().getId().equals(driver.getId())) {
            throw new RuntimeException("Access denied: Task not assigned to this driver");
        }

        // Simple State Machine Validation
        String currentStatus = event.getStatus();
        if (action.equals("ACCEPT") && !currentStatus.equals("ASSIGNED")) {
            throw new RuntimeException("Invalid transition: Cannot accept task in status " + currentStatus);
        }
        // ... more validations can be added here

        event.setStatus(newStatus);
        wasteEventRepository.save(event);
        
        if (mediaToken != null) {
            Media media = mediaService.getMediaByToken(mediaToken);
            media.setWasteEvent(event);
            // media is saved by dirty checking if managed, or explicitly if needed.
            // mediaService.getMediaByToken is readOnly, but here we are in a RW transaction.
            // So Hibernate should handle it.
        }

        DriverAction driverAction = DriverAction.builder()
                .driver(driver)
                .wasteEvent(event)
                .actionType(action)
                .note("Status changed to " + newStatus)
                .build();
        driverActionRepository.save(driverAction);
    }
}
