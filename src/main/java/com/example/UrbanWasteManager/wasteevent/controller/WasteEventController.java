package com.example.UrbanWasteManager.wasteevent.controller;

import com.example.UrbanWasteManager.wasteevent.dto.PublicWasteEventResponse;
import com.example.UrbanWasteManager.wasteevent.dto.WasteReportRequest;
import com.example.UrbanWasteManager.wasteevent.service.WasteEventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/waste-events")
@RequiredArgsConstructor
@Tag(name = "Waste Event Management", description = "APIs for reporting and viewing waste events")
public class WasteEventController {

    private final WasteEventService wasteEventService;

    @Operation(summary = "Report new waste event", description = "Allows users to report a waste issue. Can include an optional media token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event reported successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping
    public ResponseEntity<PublicWasteEventResponse> reportWaste(
            @Valid @RequestBody WasteReportRequest request,
            Authentication authentication) {
        String email = (authentication != null) ? authentication.getName() : null;
        return ResponseEntity.ok(wasteEventService.reportWaste(request, email));
    }

    @Operation(summary = "Get all waste events", description = "Retrieves all reported waste events.")
    @GetMapping
    public ResponseEntity<List<PublicWasteEventResponse>> getAllEvents() {
        return ResponseEntity.ok(wasteEventService.getAllEvents());
    }

    @Operation(summary = "Get my events", description = "Retrieves events reported by the current user.")
    @GetMapping("/my")
    public ResponseEntity<List<PublicWasteEventResponse>> getMyEvents(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(wasteEventService.getEventsByReporter(authentication.getName()));
    }

    @Operation(summary = "Get event by ID", description = "Retrieves specific waste event details.")
    @GetMapping("/{id}")
    public ResponseEntity<PublicWasteEventResponse> getEventById(@PathVariable Long id) {
        return ResponseEntity.ok(wasteEventService.getEventById(id));
    }
}
