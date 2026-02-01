package com.example.UrbanWasteManager.admin.service;

import com.example.UrbanWasteManager.admin.dto.AdminEventView;
import com.example.UrbanWasteManager.admin.dto.AdminStats;
import com.example.UrbanWasteManager.media.entity.Media;
import com.example.UrbanWasteManager.media.repository.MediaRepository;
import com.example.UrbanWasteManager.user.entity.User;
import com.example.UrbanWasteManager.user.repository.UserRepository;
import com.example.UrbanWasteManager.wasteevent.entity.WasteEvent;
import com.example.UrbanWasteManager.wasteevent.repository.WasteEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final WasteEventRepository wasteEventRepository;
    private final UserRepository userRepository;
    private final MediaRepository mediaRepository;

    @Transactional(readOnly = true)
    public List<AdminEventView> getAllEvents() {
        return wasteEventRepository.findAll().stream()
                .map(this::mapToView)
                .collect(Collectors.toList());
    }

    @Transactional
    public void assignDriver(String eventId, Long driverId) {
        WasteEvent event = getEvent(eventId);
        User driver = userRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));
        
        if (!"DRIVER".equals(driver.getRole())) {
            throw new RuntimeException("User is not a driver");
        }

        event.setAssignedDriver(driver);
        event.setStatus("ASSIGNED");
        wasteEventRepository.save(event);
        // Audit: Driver assigned
    }

    @Transactional
    public void verifyCleanup(String eventId) {
        WasteEvent event = getEvent(eventId);
        if (!"COMPLETED".equals(event.getStatus())) {
            throw new RuntimeException("Event is not in completed state");
        }
        event.setStatus("VERIFIED");
        wasteEventRepository.save(event);
        // Audit: Cleanup verified
    }

    @Transactional
    public void reopenEvent(String eventId) {
        WasteEvent event = getEvent(eventId);
        event.setStatus("REPORTED");
        event.setAssignedDriver(null);
        wasteEventRepository.save(event);
        // Audit: Event reopened
    }

    @Transactional
    public void promoteUserToDriver(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setRole("DRIVER");
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public AdminStats getStats() {
        long total = wasteEventRepository.count();
        long pending = wasteEventRepository.countByStatus("REPORTED");
        long completed = wasteEventRepository.countByStatusIn(List.of("VERIFIED", "COMPLETED"));
        long drivers = userRepository.countByRole("DRIVER");

        return AdminStats.builder()
                .totalEvents(total)
                .pendingEvents(pending)
                .completedEvents(completed)
                .totalDrivers(drivers)
                .build();
    }

    @Transactional(readOnly = true)
    public List<com.example.UrbanWasteManager.user.dto.UserResponse> getAllDrivers() {
        return userRepository.findAll().stream()
                .filter(u -> "DRIVER".equals(u.getRole()))
                .map(com.example.UrbanWasteManager.user.dto.UserResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<com.example.UrbanWasteManager.user.dto.UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(com.example.UrbanWasteManager.user.dto.UserResponse::fromEntity)
                .collect(Collectors.toList());
    }

    private WasteEvent getEvent(String eventId) {
        return wasteEventRepository.findByPublicId(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
    }

    private AdminEventView mapToView(WasteEvent event) {
        List<String> mediaTokens = mediaRepository.findAllByWasteEventId(event.getId()).stream()
                .map(Media::getToken)
                .collect(Collectors.toList());

        return AdminEventView.builder()
                .eventId(event.getPublicId())
                .reporterEmail(event.getReporter() != null ? event.getReporter().getEmail() : "Anonymous")
                .assignedDriverName(event.getAssignedDriver() != null ? event.getAssignedDriver().getFirstName() + " " + event.getAssignedDriver().getLastName() : "Unassigned")
                .status(event.getStatus())
                .createdAt(event.getCreatedAt())
                .lastUpdatedAt(event.getUpdatedAt())
                .internalNote(null) // Placeholder
                .description(event.getDescription())
                .latitude(event.getLatitude())
                .longitude(event.getLongitude())
                .mediaTokens(mediaTokens)
                .build();
    }
}
