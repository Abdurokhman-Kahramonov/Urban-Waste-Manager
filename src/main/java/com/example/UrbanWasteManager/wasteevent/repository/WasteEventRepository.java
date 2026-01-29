package com.example.UrbanWasteManager.wasteevent.repository;

import com.example.UrbanWasteManager.wasteevent.entity.WasteEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface WasteEventRepository extends JpaRepository<WasteEvent, Long> {
    List<WasteEvent> findAllByStatus(String status);
    Optional<WasteEvent> findByPublicId(String publicId);
}
