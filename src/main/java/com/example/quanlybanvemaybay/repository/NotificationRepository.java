package com.example.quanlybanvemaybay.repository;

import com.example.quanlybanvemaybay.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);
    int countByUserIdAndIsReadFalse(Long userId);
    List<Notification> findByGroupId(String groupId);
    void deleteByGroupId(String groupId);
    List<Notification> findAllByOrderByCreatedAtDesc();
    List<Notification> findByTitleAndMessage(String title, String message);
    void deleteByTitleAndMessage(String title, String message);
}
