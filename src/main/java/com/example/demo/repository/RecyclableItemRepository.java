package com.example.demo.repository;

import com.example.demo.entity.RecyclableItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RecyclableItemRepository extends JpaRepository<RecyclableItem, Long> {

    List<RecyclableItem> findByBatchId(Long batchId);

    @Query("SELECT i FROM RecyclableItem i WHERE i.batch IS NULL AND (i.request.status = 'COLLECTED' OR i.request.status = 'APPROVED' OR i.request.status = 'UNDER_INSPECTION')")
    List<RecyclableItem> findAvailableForBatching();
}
