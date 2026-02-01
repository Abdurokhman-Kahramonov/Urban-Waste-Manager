package com.example.UrbanWasteManager.admin.service;

import com.example.UrbanWasteManager.admin.dto.metrics.MetricsResponse;
import com.example.UrbanWasteManager.user.entity.User;
import com.example.UrbanWasteManager.user.repository.UserRepository;
import com.example.UrbanWasteManager.wasteevent.repository.WasteEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminMetricsService {

    private final WasteEventRepository wasteEventRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public MetricsResponse getDashboardMetrics() {
        return MetricsResponse.builder()
                .overview(buildOverview())
                .volumeChart(buildVolumeChart())
                .typeChart(buildTypeChart())
                .statusChart(buildStatusChart())
                .heatMapData(buildGeoData())
                .driverTable(buildDriverTable())
                .build();
    }

    private MetricsResponse.OverviewMetrics buildOverview() {
        // In a real scenario, calculate these from DB timestamps
        // For now, we'll calculate basic counts
        long total = wasteEventRepository.count();
        long completed = wasteEventRepository.countByStatus("COMPLETED");
        double rate = total > 0 ? ((double) completed / total) * 100 : 0;

        return MetricsResponse.OverviewMetrics.builder()
                .avgResponseTime("24m") // Placeholder complexity
                .avgResolutionTime("4.2h") // Placeholder complexity
                .completionRate(Math.round(rate * 10.0) / 10.0)
                .build();
    }

    private MetricsResponse.ChartData buildVolumeChart() {
        List<Object[]> results = wasteEventRepository.findDailyEventVolume();
        List<String> labels = new ArrayList<>();
        List<Number> data = new ArrayList<>();

        for (Object[] row : results) {
            labels.add(row[0].toString());
            data.add((Number) row[1]);
        }

        return MetricsResponse.ChartData.builder()
                .labels(labels)
                .data(data)
                .build();
    }

    private MetricsResponse.ChartData buildTypeChart() {
        List<Object[]> results = wasteEventRepository.findEventTypeDistribution();
        List<String> labels = new ArrayList<>();
        List<Number> data = new ArrayList<>();

        for (Object[] row : results) {
            labels.add(row[0] != null ? row[0].toString().replace("_", " ") : "Unknown");
            data.add((Number) row[1]);
        }

        return MetricsResponse.ChartData.builder()
                .labels(labels)
                .data(data)
                .build();
    }

    private MetricsResponse.ChartData buildStatusChart() {
        List<Object[]> results = wasteEventRepository.findStatusDistribution();
        // Group into Open (Reported, Assigned, Accepted) vs Closed (Completed, Verified)
        // Simplified for this chart
        long open = 0;
        long closed = 0;

        for (Object[] row : results) {
            String status = (String) row[0];
            long count = ((Number) row[1]).longValue();
            if ("COMPLETED".equals(status) || "VERIFIED".equals(status)) {
                closed += count;
            } else {
                open += count;
            }
        }

        return MetricsResponse.ChartData.builder()
                .labels(List.of("Open", "Closed"))
                .data(List.of(open, closed))
                .build();
    }

    private List<MetricsResponse.GeoPoint> buildGeoData() {
        List<Object[]> results = wasteEventRepository.findAllGeoPoints();
        return results.stream()
                .map(row -> MetricsResponse.GeoPoint.builder()
                        .lat((Double) row[0])
                        .lng((Double) row[1])
                        .build())
                .collect(Collectors.toList());
    }

    private List<MetricsResponse.DriverPerfRow> buildDriverTable() {
        List<Object[]> results = wasteEventRepository.findCompletedTasksPerDriver();
        List<MetricsResponse.DriverPerfRow> rows = new ArrayList<>();
        
        for (Object[] row : results) {
            Long driverId = (Long) row[0];
            Long count = (Long) row[1];
            if (driverId == null) continue;

            String name = userRepository.findById(driverId)
                    .map(u -> u.getFirstName() + " " + u.getLastName())
                    .orElse("Unknown Driver");

            rows.add(MetricsResponse.DriverPerfRow.builder()
                    .name(name)
                    .completedTasks(count)
                    .build());
        }
        return rows;
    }
}
