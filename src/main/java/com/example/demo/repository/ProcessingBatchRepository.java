package com.example.demo.repository;

import com.example.demo.entity.ProcessingBatch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProcessingBatchRepository extends JpaRepository<ProcessingBatch, Long> {
    List<ProcessingBatch> findByFacilityIdAndStatus(Long facilityId, ProcessingBatch.BatchStatus status);
}
