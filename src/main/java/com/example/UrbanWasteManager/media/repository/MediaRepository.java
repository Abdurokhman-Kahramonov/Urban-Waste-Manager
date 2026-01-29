package com.example.UrbanWasteManager.media.repository;

import com.example.UrbanWasteManager.media.entity.Media;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MediaRepository extends JpaRepository<Media, Long> {
    Optional<Media> findByToken(String token);
}
