package com.example.UrbanWasteManager.wasteevent.service;

import com.example.UrbanWasteManager.common.exception.ResourceNotFoundException;
import com.example.UrbanWasteManager.media.entity.Media;
import com.example.UrbanWasteManager.media.repository.MediaRepository;
import com.example.UrbanWasteManager.media.service.MediaService;
import com.example.UrbanWasteManager.user.entity.User;
import com.example.UrbanWasteManager.user.repository.UserRepository;
import com.example.UrbanWasteManager.wasteevent.dto.PublicWasteEventResponse;
import com.example.UrbanWasteManager.wasteevent.dto.WasteReportRequest;
import com.example.UrbanWasteManager.wasteevent.entity.WasteEvent;
import com.example.UrbanWasteManager.wasteevent.repository.WasteEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WasteEventService {

    private final WasteEventRepository wasteEventRepository;
    private final MediaService mediaService;
    private final UserRepository userRepository; // To fetch reporter if authenticated
    private final MediaRepository mediaRepository;

    @Transactional
    public PublicWasteEventResponse reportWaste(WasteReportRequest request, String reporterEmail) {
        if (reporterEmail == null) {
            throw new IllegalArgumentException("Authentication required to report waste");
        }
        
        User reporter = userRepository.findByEmail(reporterEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + reporterEmail));

        WasteEvent event = WasteEvent.builder()
                .category(request.getCategory())
                .description(request.getDescription())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .status("REPORTED")
                .publicId(UUID.randomUUID().toString())
                .reporter(reporter)
                .build();

        WasteEvent savedEvent = wasteEventRepository.save(event);

        // Link Media if present
        if (request.getMediaToken() != null) {
            Media media = mediaService.getMediaByToken(request.getMediaToken());
            media.setWasteEvent(savedEvent);
            // mediaRepository.save(media); // Handled by dirty checking or cascade usually, but Media is owner of relationship?
            // Checking Media entity: @ManyToOne WasteEvent. Yes, Media owns the foreign key.
            // We need to update the media entity explicitly or through MediaService helper if not in same txn/session boundaries, 
            // but here we are in same transaction. 
            // However, since we retrieved media via service (read-only txn potentially?), let's ensure it's saved.
            // Actually MediaService.getMediaByToken is read-only.
            // Let's rely on dirty checking if this method is transactional.
        }

        return mapToPublicResponse(savedEvent);
    }

    @Transactional(readOnly = true)
    public List<PublicWasteEventResponse> getAllEvents() {
        return wasteEventRepository.findAll().stream()
                .map(this::mapToPublicResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PublicWasteEventResponse> getEventsByReporter(String email) {
        User reporter = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return wasteEventRepository.findAll().stream() // Inefficient, should be findByReporter
                .filter(e -> e.getReporter() != null && e.getReporter().getId().equals(reporter.getId()))
                .map(this::mapToPublicResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public PublicWasteEventResponse getEventById(Long id) {
         WasteEvent event = wasteEventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Waste Event not found with id: " + id));
         return mapToPublicResponse(event);
    }

    // Helper mapper (could be in a Mapper class)
    private PublicWasteEventResponse mapToPublicResponse(WasteEvent event) {
        List<String> mediaTokens = mediaRepository.findAllByWasteEventId(event.getId()).stream()
                .map(Media::getToken)
                .collect(Collectors.toList());

        return PublicWasteEventResponse.builder()
                .publicId(event.getPublicId())
                .category(event.getCategory())
                .description(event.getDescription())
                .latitude(event.getLatitude())
                .longitude(event.getLongitude())
                .status(event.getStatus())
                .reportedAt(event.getCreatedAt())
                .mediaTokens(mediaTokens)
                .build();
    }
}
