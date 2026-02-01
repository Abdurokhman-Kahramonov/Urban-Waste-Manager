package com.example.UrbanWasteManager.driver.controller;

import com.example.UrbanWasteManager.driver.dto.CompleteTaskRequest;
import com.example.UrbanWasteManager.driver.dto.DriverTaskDetail;
import com.example.UrbanWasteManager.driver.dto.DriverTaskSummary;
import com.example.UrbanWasteManager.driver.service.DriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/driver/tasks")
@RequiredArgsConstructor
public class DriverController {

    private final DriverService driverService;

    @GetMapping
    public ResponseEntity<List<DriverTaskSummary>> getAssignedTasks() {
        return ResponseEntity.ok(driverService.getAssignedTasks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DriverTaskDetail> getTaskDetail(@PathVariable String id) {
        return ResponseEntity.ok(driverService.getTaskDetail(id));
    }

    @PostMapping("/{id}/accept")
    public ResponseEntity<Void> acceptTask(@PathVariable String id) {
        driverService.updateTaskStatus(id, "ACCEPT", "ACCEPTED");
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<Void> completeTask(@PathVariable String id, @RequestBody(required = false) CompleteTaskRequest request) {
        String mediaToken = (request != null) ? request.getMediaToken() : null;
        driverService.updateTaskStatus(id, "COMPLETE", "COMPLETED", mediaToken);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/fail")
    public ResponseEntity<Void> failTask(@PathVariable String id) {
        driverService.updateTaskStatus(id, "FAIL", "REPORTED"); // Revert to reported or a specific ERROR status
        return ResponseEntity.ok().build();
    }
}
