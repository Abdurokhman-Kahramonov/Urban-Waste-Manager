package com.example.UrbanWasteManager.admin.controller;

import com.example.UrbanWasteManager.admin.dto.AdminEventView;
import com.example.UrbanWasteManager.admin.dto.AdminStats;
import com.example.UrbanWasteManager.admin.dto.AssignDriverRequest;
import com.example.UrbanWasteManager.admin.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/events")
    public ResponseEntity<List<AdminEventView>> getAllEvents() {
        return ResponseEntity.ok(adminService.getAllEvents());
    }

    @PostMapping("/events/{id}/assign")
    public ResponseEntity<Void> assignDriver(@PathVariable String id, @RequestBody @Valid AssignDriverRequest request) {
        adminService.assignDriver(id, request.getDriverId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/events/{id}/verify")
    public ResponseEntity<Void> verifyCleanup(@PathVariable String id) {
        adminService.verifyCleanup(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/events/{id}/reopen")
    public ResponseEntity<Void> reopenEvent(@PathVariable String id) {
        adminService.reopenEvent(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/users/{id}/promote")
    public ResponseEntity<Void> promoteUser(@PathVariable Long id) {
        adminService.promoteUserToDriver(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/stats")
    public ResponseEntity<AdminStats> getStats() {
        return ResponseEntity.ok(adminService.getStats());
    }

    @GetMapping("/drivers")
    public ResponseEntity<List<com.example.UrbanWasteManager.user.dto.UserResponse>> getAllDrivers() {
        return ResponseEntity.ok(adminService.getAllDrivers());
    }

    @GetMapping("/users")
    public ResponseEntity<List<com.example.UrbanWasteManager.user.dto.UserResponse>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }
}
