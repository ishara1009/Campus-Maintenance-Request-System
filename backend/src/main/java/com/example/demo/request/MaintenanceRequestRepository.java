package com.example.demo.request;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MaintenanceRequestRepository extends JpaRepository<MaintenanceRequest, Long> {
    List<MaintenanceRequest> findByUserIdOrderByCreatedAtDesc(Long userId);
    long countByUserId(Long userId);
    long countByUserIdAndStatus(Long userId, MaintenanceRequest.Status status);
    List<MaintenanceRequest> findAllByOrderByCreatedAtDesc();
    List<MaintenanceRequest> findByAssigneeIdOrderByCreatedAtDesc(Long assigneeId);
    Optional<MaintenanceRequest> findByIdAndUserId(Long id,Long userId);
    boolean existsByAssigneeIdAndStatusNot(Long assigneeId,MaintenanceRequest.Status status);
    List<MaintenanceRequest> findByAssigneeIdAndStatusNot(Long assigneeId,MaintenanceRequest.Status status);
}
