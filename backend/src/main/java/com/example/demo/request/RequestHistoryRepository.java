package com.example.demo.request;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface RequestHistoryRepository extends JpaRepository<RequestHistory,Long>{List<RequestHistory> findByRequestIdOrderByCreatedAtAsc(Long requestId);}
