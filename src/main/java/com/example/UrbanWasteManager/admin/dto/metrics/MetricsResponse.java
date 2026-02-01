package com.example.UrbanWasteManager.admin.dto.metrics;

import lombok.Builder;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class MetricsResponse {
    private OverviewMetrics overview;
    private ChartData volumeChart;
    private ChartData typeChart;
    private ChartData statusChart;
    private List<GeoPoint> heatMapData;
    private List<DriverPerfRow> driverTable;

    @Data
    @Builder
    public static class OverviewMetrics {
        private String avgResponseTime;
        private String avgResolutionTime;
        private double completionRate;
    }

    @Data
    @Builder
    public static class ChartData {
        private List<String> labels;
        private List<Number> data;
    }

    @Data
    @Builder
    public static class GeoPoint {
        private Double lat;
        private Double lng;
    }
    
    @Data
    @Builder
    public static class DriverPerfRow {
        private String name;
        private Long completedTasks;
    }
}
