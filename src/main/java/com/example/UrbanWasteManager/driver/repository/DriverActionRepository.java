package com.example.UrbanWasteManager.driver.repository;

import com.example.UrbanWasteManager.driver.entity.DriverAction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DriverActionRepository extends JpaRepository<DriverAction, Long> {
    List<DriverAction> findAllByDriverId(Long driverId);
}
