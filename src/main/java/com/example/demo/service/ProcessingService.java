package com.example.demo.service;

import com.example.demo.entity.ProcessingBatch;
import com.example.demo.entity.RecyclableItem;
import com.example.demo.entity.RecyclingFacility;
import com.example.demo.exception.BusinessValidationException;
import com.example.demo.repository.ProcessingBatchRepository;
import com.example.demo.repository.RecyclableItemRepository;
import com.example.demo.repository.RecyclingFacilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProcessingService {

    private final ProcessingBatchRepository batchRepository;
    private final RecyclableItemRepository itemRepository;
    private final RecyclingFacilityRepository facilityRepository;

    @Transactional
    public ProcessingBatch createBatch(Long facilityId, List<Long> itemIds) {
        RecyclingFacility facility = facilityRepository.findById(facilityId)
                .orElseThrow(() -> new BusinessValidationException("Facility not found"));

        List<RecyclableItem> items = itemRepository.findAllById(itemIds);

        double totalWeight = 0.0;
        for (RecyclableItem item : items) {
            if (item.getActualWeight() != null) {
                totalWeight += item.getActualWeight();
            }
        }

        Double currentOccupancy = facility.getCurrentOccupancyKg() == null ? 0.0 : facility.getCurrentOccupancyKg();
        if (currentOccupancy + totalWeight > facility.getMaxCapacityKg()) {
            throw new BusinessValidationException("Batch exceeds facility capacity!");
        }

        ProcessingBatch batch = ProcessingBatch.builder()
                .facility(facility)
                .startDate(LocalDateTime.now())
                .status(ProcessingBatch.BatchStatus.IN_PROGRESS)
                .totalBatchWeight(totalWeight)
                .build();

        ProcessingBatch savedBatch = batchRepository.save(batch);

        for (RecyclableItem item : items) {
            if (item.getBatch() != null) {
                throw new BusinessValidationException("Item " + item.getId() + " is already in a batch");
            }
            item.setBatch(savedBatch);
            item.setFacility(facility);
        }
        itemRepository.saveAll(items);

        return savedBatch;
    }

    @Transactional
    public ProcessingBatch completeBatch(Long batchId) {
        ProcessingBatch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new BusinessValidationException("Batch not found"));

        if (batch.getStatus() != ProcessingBatch.BatchStatus.IN_PROGRESS) {
            throw new BusinessValidationException("Only in-progress batches can be completed");
        }

        batch.setStatus(ProcessingBatch.BatchStatus.COMPLETED);
        batch.setEndDate(LocalDateTime.now());

        RecyclingFacility facility = batch.getFacility();
        Double currentOccupancy = facility.getCurrentOccupancyKg() == null ? 0.0 : facility.getCurrentOccupancyKg();
        Double batchWeight = batch.getTotalBatchWeight() == null ? 0.0 : batch.getTotalBatchWeight();
        facility.setCurrentOccupancyKg(currentOccupancy - batchWeight);
        facilityRepository.save(facility);

        return batchRepository.save(batch);
    }

    public List<ProcessingBatch> getAllBatches() {
        return batchRepository.findAll();
    }

    public List<RecyclableItem> getAvailableItems() {
    List<RecyclableItem> items = itemRepository.findAvailableForBatching();

    System.out.println("Available items: " + items.size());

    for (RecyclableItem item : items) {
        System.out.println(
            "Item ID = " + item.getId()
            + ", Status = " + item.getRequest().getStatus()
        );
    }

    return items;
}

    public List<RecyclableItem> getAllItemsForProcessing() {
        return itemRepository.findAvailableForBatching();
    }
}
