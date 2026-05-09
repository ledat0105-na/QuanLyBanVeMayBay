package com.example.quanlybanvemaybay.repository;

import com.example.quanlybanvemaybay.entity.MediaAsset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MediaAssetRepository extends JpaRepository<MediaAsset, Long> {
    List<MediaAsset> findByImageTypeOrderByUploadedAtDesc(String imageType);
}
