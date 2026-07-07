package com.example.demo.service;

import com.example.demo.dto.CollectionRequestDto;
import com.example.demo.entity.CollectionRequest;
import com.example.demo.entity.RecyclableItem;
import com.example.demo.entity.RecyclingFacility;
import com.example.demo.entity.SystemUser;
import com.example.demo.exception.BusinessValidationException;
import com.example.demo.repository.CollectionRequestRepository;
import com.example.demo.repository.RecyclableItemRepository;
import com.example.demo.repository.RecyclingFacilityRepository;
import com.example.demo.repository.SystemUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CollectionService {

    private final CollectionRequestRepository repository;
    private final SystemUserRepository userRepository;
    private final RecyclableItemRepository itemRepository;
    private final RecyclingFacilityRepository facilityRepository;

    @Transactional
    public SystemUser updateContributorWaste(Long userId, Double organic, Double recyclable, Double nonRecyclable, Double hazardous) {
        SystemUser user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessValidationException("User not found"));

        user.setOrganicWaste(organic);
        user.setRecyclableWaste(recyclable);
        user.setNonRecyclableWaste(nonRecyclable);
        user.setHazardousWaste(hazardous);

        return userRepository.save(user);
    }

    @Transactional
    public CollectionRequest createRequest(CollectionRequestDto dto, Long userId) {
        SystemUser contributor = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessValidationException("User not found"));

        CollectionRequest request = CollectionRequest.builder()
                .contributor(contributor)
                .pickupAddress(dto.getPickupAddress())
                .scheduledDate(LocalDate.parse(dto.getScheduledDate()))
                .totalWeightEstimate(dto.getTotalWeightEstimate())
                .status(CollectionRequest.RequestStatus.SUBMITTED)
                .build();

        if (dto.getItems() != null) {
            List<RecyclableItem> items = dto.getItems().stream()
                    .map(itemDto -> RecyclableItem.builder()
                            .materialType(itemDto.getMaterialType())
                            .actualWeight(itemDto.getActualWeight())
                            .request(request)
                            .build())
                    .collect(Collectors.toList());
            request.setItems(items);
        }

        Double pending = contributor.getPendingWasteKg() == null ? 0.0 : contributor.getPendingWasteKg();
        Double estimate = dto.getTotalWeightEstimate() == null ? 0.0 : dto.getTotalWeightEstimate();
        contributor.setPendingWasteKg(pending + estimate);
        userRepository.save(contributor);

        return repository.save(request);
    }

    @Transactional
    public CollectionRequest assignAgent(Long requestId, Long agentId) {
        CollectionRequest request = repository.findById(requestId)
                .orElseThrow(() -> new BusinessValidationException("Request not found"));

        SystemUser agent = userRepository.findById(agentId)
                .orElseThrow(() -> new BusinessValidationException("Agent not found"));

        if (request.getStatus() != CollectionRequest.RequestStatus.SUBMITTED) {
            throw new BusinessValidationException("Request cannot be assigned in current status: " + request.getStatus());
        }

        request.setAgent(agent);
        request.setStatus(CollectionRequest.RequestStatus.ASSIGNED);

        return repository.save(request);
    }

    public List<CollectionRequest> getMyRequests(Long userId) {
        return repository.findByContributorId(userId);
    }

    public List<CollectionRequest> getAgentRequests(Long agentId) {
        List<CollectionRequest> result = new ArrayList<>(repository.findByStatus(CollectionRequest.RequestStatus.SUBMITTED));
        result.addAll(repository.findByAgentIdAndStatus(agentId, CollectionRequest.RequestStatus.ASSIGNED));
        return result;
    }

    public List<CollectionRequest> getAllRequests() {
        return repository.findAll();
    }

    public CollectionRequest getRequestById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new BusinessValidationException("Request not found"));
    }

    @Transactional
    public CollectionRequest markAsCollected(Long requestId, Long agentId, Long facilityId) {
        CollectionRequest request = repository.findById(requestId)
                .orElseThrow(() -> new BusinessValidationException("Request not found"));

        SystemUser agent = userRepository.findById(agentId)
                .orElseThrow(() -> new BusinessValidationException("Agent not found"));

        RecyclingFacility facility = facilityRepository.findById(facilityId)
                .orElseThrow(() -> new BusinessValidationException("Facility not found"));

        if (request.getStatus() != CollectionRequest.RequestStatus.ASSIGNED
                && request.getStatus() != CollectionRequest.RequestStatus.SUBMITTED) {
            throw new BusinessValidationException("Request must be SUBMITTED or ASSIGNED to be collected");
        }

        request.setAgent(agent);

        double totalWeight = 0.0;
        if (request.getItems() != null) {
            for (RecyclableItem item : request.getItems()) {
                if (item.getActualWeight() != null) {
                    totalWeight += item.getActualWeight();
                }
                item.setFacility(facility);
            }
            itemRepository.saveAll(request.getItems());
        }

        Double currentOccupancy = facility.getCurrentOccupancyKg() == null ? 0.0 : facility.getCurrentOccupancyKg();
        facility.setCurrentOccupancyKg(currentOccupancy + totalWeight);
        facilityRepository.save(facility);

        SystemUser contributor = request.getContributor();
        contributor.setOrganicWaste(0.0);
        contributor.setRecyclableWaste(0.0);
        contributor.setNonRecyclableWaste(0.0);
        contributor.setHazardousWaste(0.0);

        Double collectedBalance = agent.getCollectedWasteBalance() == null ? 0.0 : agent.getCollectedWasteBalance();
        agent.setCollectedWasteBalance(collectedBalance + totalWeight);
        userRepository.save(agent);

        Double totalContributed = contributor.getTotalContributedKg() == null ? 0.0 : contributor.getTotalContributedKg();
        contributor.setTotalContributedKg(totalContributed + totalWeight);

        Double pending = contributor.getPendingWasteKg() == null ? 0.0 : contributor.getPendingWasteKg();
        Double estimate = request.getTotalWeightEstimate() == null ? 0.0 : request.getTotalWeightEstimate();
        contributor.setPendingWasteKg(Math.max(0, pending - estimate));
        userRepository.save(contributor);

        request.setStatus(CollectionRequest.RequestStatus.COLLECTED);

        return repository.save(request);
    }

    @Transactional
    public void requestBulkInspection(Long agentId) {
        List<CollectionRequest> collected = repository.findByAgentIdAndStatus(agentId, CollectionRequest.RequestStatus.COLLECTED);

        if (collected.isEmpty()) {
            throw new BusinessValidationException("No collected requests found for inspection");
        }

        collected.forEach(r -> r.setStatus(CollectionRequest.RequestStatus.UNDER_INSPECTION));
        repository.saveAll(collected);
    }

    @Transactional
    public CollectionRequest approveInspection(Long requestId) {
        CollectionRequest request = repository.findById(requestId)
                .orElseThrow(() -> new BusinessValidationException("Request not found"));

        if (request.getStatus() != CollectionRequest.RequestStatus.UNDER_INSPECTION) {
            throw new BusinessValidationException("Only requests under inspection can be approved");
        }

        double totalWeight = 0.0;
        if (request.getItems() != null) {
            for (RecyclableItem item : request.getItems()) {
                if (item.getActualWeight() != null) {
                    totalWeight += item.getActualWeight();
                }
            }
        }

        request.setStatus(CollectionRequest.RequestStatus.APPROVED);
        request.setRewardPointsEarned(totalWeight * 10.0);

        SystemUser contributor = request.getContributor();
        Double rewardPoints = contributor.getRewardPoints() == null ? 0.0 : contributor.getRewardPoints();
        contributor.setRewardPoints(rewardPoints + request.getRewardPointsEarned());
        userRepository.save(contributor);

        return repository.save(request);
    }

    @Transactional
    public void deleteRequest(Long requestId) {
        repository.deleteById(requestId);
    }
}
