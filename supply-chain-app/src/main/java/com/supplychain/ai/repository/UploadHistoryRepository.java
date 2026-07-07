package com.supplychain.ai.repository;

import com.supplychain.ai.model.UploadHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UploadHistoryRepository extends JpaRepository<UploadHistory, Long> {
    List<UploadHistory> findAllByOrderByUploadTimeDesc();
}
