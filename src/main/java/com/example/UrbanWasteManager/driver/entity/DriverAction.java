package com.example.UrbanWasteManager.driver.entity;

import com.example.UrbanWasteManager.user.entity.User;
import com.example.UrbanWasteManager.wasteevent.entity.WasteEvent;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "driver_actions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = false)
    private User driver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "waste_event_id", nullable = false)
    private WasteEvent wasteEvent;

    @Column(nullable = false)
    private String actionType; // ACCEPT, COMPLETE, FAIL

    @Column(length = 500)
    private String note;

    private LocalDateTime timestamp;

    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
    }
}
