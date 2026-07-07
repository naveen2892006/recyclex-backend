package com.example.demo.service;

import com.example.demo.entity.CollectionRequest;
import com.example.demo.entity.ProcessingBatch;
import com.example.demo.entity.RecyclableItem;
import com.example.demo.entity.RecyclingFacility;
import com.example.demo.repository.CollectionRequestRepository;
import com.example.demo.repository.ProcessingBatchRepository;
import com.example.demo.repository.RecyclingFacilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final CollectionRequestRepository collectionRequestRepository;
    private final ProcessingBatchRepository processingBatchRepository;
    private final RecyclingFacilityRepository facilityRepository;

    public Map<String, Object> getGlobalStats() {

        List<CollectionRequest> allRequests = collectionRequestRepository.findAll();

        double totalWasteCollected = allRequests.stream()
                .filter(r ->
                        r.getStatus() == CollectionRequest.RequestStatus.APPROVED ||
                        r.getStatus() == CollectionRequest.RequestStatus.COLLECTED ||
                        r.getStatus() == CollectionRequest.RequestStatus.UNDER_INSPECTION)
                .flatMap(r -> r.getItems() == null ? Stream.empty() : r.getItems().stream())
                .mapToDouble(item ->
                        item.getActualWeight() == null ? 0.0 : item.getActualWeight())
                .sum();

        List<ProcessingBatch> allBatches = processingBatchRepository.findAll();

        double totalWasteRecycled = allBatches.stream()
                .filter(batch ->
                        batch.getStatus() == ProcessingBatch.BatchStatus.COMPLETED)
                .mapToDouble(batch ->
                        batch.getTotalBatchWeight() == null ? 0.0 : batch.getTotalBatchWeight())
                .sum();

        double totalRewardsOffered = allRequests.stream()
                .filter(r ->
                        r.getStatus() == CollectionRequest.RequestStatus.APPROVED)
                .mapToDouble(r ->
                        r.getRewardPointsEarned() == null ? 0.0 : r.getRewardPointsEarned())
                .sum();

        long activeRequestsCount = allRequests.stream()
                .filter(r ->
                        r.getStatus() != CollectionRequest.RequestStatus.APPROVED &&
                        r.getStatus() != CollectionRequest.RequestStatus.CANCELLED)
                .count();

        List<Map<String, Object>> facilityUtilization = new ArrayList<>();

        for (RecyclingFacility facility : facilityRepository.findAll()) {

            Map<String, Object> map = new HashMap<>();

            map.put("name", facility.getName());
            map.put("currentLoad", facility.getCurrentOccupancyKg());
            map.put("maximumCapacity", facility.getMaxCapacityKg());

            facilityUtilization.add(map);
        }

        Map<String, Double> materialDistribution = new HashMap<>();

        for (CollectionRequest request : allRequests) {

            if (request.getItems() != null) {

                for (RecyclableItem item : request.getItems()) {

                    String material = item.getMaterialType().name();

                    Double weight = item.getActualWeight() == null
                            ? 0.0
                            : item.getActualWeight();

                    materialDistribution.put(
                            material,
                            materialDistribution.getOrDefault(material, 0.0) + weight
                    );
                }
            }
        }

        Map<String, Object> stats = new HashMap<>();

        stats.put("totalWasteCollected", totalWasteCollected);
        stats.put("totalWasteRecycled", totalWasteRecycled);
        stats.put("totalRewardsOffered", totalRewardsOffered);
        stats.put("activeRequestsCount", activeRequestsCount);
        stats.put("facilityUtilization", facilityUtilization);
        stats.put("materialDistribution", materialDistribution);

        return stats;
    }
}