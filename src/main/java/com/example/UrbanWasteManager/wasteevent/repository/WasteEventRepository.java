package com.example.UrbanWasteManager.wasteevent.repository;

import com.example.UrbanWasteManager.wasteevent.entity.WasteEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface WasteEventRepository extends JpaRepository<WasteEvent, Long> {
    List<WasteEvent> findAllByStatus(String status);
    Optional<WasteEvent> findByPublicId(String publicId);
    List<WasteEvent> findAllByAssignedDriverId(Long driverId);
    long countByAssignedDriverIdAndStatus(Long driverId, String status);
    long countByStatus(String status);
    long countByStatusIn(List<String> statuses);

    // --- Analytics Queries ---

    // 1. Daily Volume (Last 7 Days)
    @org.springframework.data.jpa.repository.Query(value = "SELECT CAST(created_at AS DATE) as date, COUNT(*) as count FROM waste_events WHERE created_at >= CURRENT_DATE - INTERVAL '7 days' GROUP BY CAST(created_at AS DATE) ORDER BY date ASC", nativeQuery = true)
    List<Object[]> findDailyEventVolume();

    // 2. Event Type Distribution
    @org.springframework.data.jpa.repository.Query("SELECT e.category, COUNT(e) FROM WasteEvent e GROUP BY e.category")
    List<Object[]> findEventTypeDistribution();

    // 3. Status Distribution (Open vs Closed)
    @org.springframework.data.jpa.repository.Query("SELECT e.status, COUNT(e) FROM WasteEvent e GROUP BY e.status")
    List<Object[]> findStatusDistribution();

    // 4. Geo Points
    @org.springframework.data.jpa.repository.Query("SELECT e.latitude, e.longitude FROM WasteEvent e")
    List<Object[]> findAllGeoPoints();

    // 5. Driver Performance Stats (Completed Tasks)
    @org.springframework.data.jpa.repository.Query("SELECT e.assignedDriver.id, COUNT(e) FROM WasteEvent e WHERE e.status = 'COMPLETED' GROUP BY e.assignedDriver.id")
    List<Object[]> findCompletedTasksPerDriver();
}
