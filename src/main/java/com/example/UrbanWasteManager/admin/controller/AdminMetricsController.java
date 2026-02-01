package com.example.UrbanWasteManager.admin.controller;

import com.example.UrbanWasteManager.admin.dto.metrics.MetricsResponse;
import com.example.UrbanWasteManager.admin.service.AdminMetricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/metrics")
@RequiredArgsConstructor
public class AdminMetricsController {
    private final AdminMetricsService adminMetricsService;
    @GetMapping("/dashboard")
    public ResponseEntity<MetricsResponse> getDashboardMetrics() {
        return ResponseEntity.ok(adminMetricsService.getDashboardMetrics());
    }
}
