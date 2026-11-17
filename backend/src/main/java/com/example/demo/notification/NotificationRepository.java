package com.example.demo.notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface NotificationRepository extends JpaRepository<Notification,Long>{List<Notification> findByUserIdOrderByCreatedAtDesc(Long id);Optional<Notification> findByIdAndUserId(Long id,Long userId);long countByUserIdAndReadFalse(Long id);}
