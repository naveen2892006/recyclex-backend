package com.example.demo.controller;

import com.example.demo.dto.CollectionRequestDto;
import com.example.demo.entity.CollectionRequest;
import com.example.demo.entity.SystemUser;
import com.example.demo.service.CollectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/collections")
@RequiredArgsConstructor
public class CollectionController {

    private final CollectionService collectionService;

    @GetMapping
    @PreAuthorize("hasAnyRole('RECYCLING_COORDINATOR', 'FACILITY_MANAGER')")
    public ResponseEntity<List<CollectionRequest>> getAll() {
        return new ResponseEntity<>(collectionService.getAllRequests(), HttpStatus.OK);
    }

    @PostMapping("/{userId}")
    @PreAuthorize("hasRole('CONTRIBUTOR')")
    public ResponseEntity<CollectionRequest> createRequest(@PathVariable Long userId,
                                                             @Valid @RequestBody CollectionRequestDto dto) {
        return new ResponseEntity<>(collectionService.createRequest(dto, userId), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CONTRIBUTOR', 'COLLECTION_AGENT', 'RECYCLING_COORDINATOR', 'FACILITY_MANAGER')")
    public ResponseEntity<CollectionRequest> getById(@PathVariable Long id) {
        return new ResponseEntity<>(collectionService.getRequestById(id), HttpStatus.OK);
    }

    @PutMapping("/{id}/assign")
    @PreAuthorize("hasRole('RECYCLING_COORDINATOR')")
    public ResponseEntity<CollectionRequest> assign(@PathVariable Long id, @RequestParam Long agentId) {
        return new ResponseEntity<>(collectionService.assignAgent(id, agentId), HttpStatus.OK);
    }

    @GetMapping("/my")
    public ResponseEntity<List<CollectionRequest>> getMy(@RequestParam Long userId) {
        return new ResponseEntity<>(collectionService.getMyRequests(userId), HttpStatus.OK);
    }

    @GetMapping("/agent")
    @PreAuthorize("hasRole('COLLECTION_AGENT')")
    public ResponseEntity<List<CollectionRequest>> getAgentTasks(@RequestParam Long agentId) {
        return new ResponseEntity<>(collectionService.getAgentRequests(agentId), HttpStatus.OK);
    }

    @PutMapping("/user/{userId}/waste")
    @PreAuthorize("hasRole('CONTRIBUTOR')")
    public ResponseEntity<SystemUser> updateWaste(@PathVariable Long userId,
                                                    @RequestParam Double organic,
                                                    @RequestParam Double recyclable,
                                                    @RequestParam Double nonRecyclable,
                                                    @RequestParam Double hazardous) {
        return new ResponseEntity<>(
                collectionService.updateContributorWaste(userId, organic, recyclable, nonRecyclable, hazardous),
                HttpStatus.OK);
    }

    @PutMapping("/{id}/collect")
    @PreAuthorize("hasRole('COLLECTION_AGENT')")
    public ResponseEntity<CollectionRequest> collect(@PathVariable Long id,
                                                       @RequestParam Long agentId,
                                                       @RequestParam Long facilityId) {
        return new ResponseEntity<>(collectionService.markAsCollected(id, agentId, facilityId), HttpStatus.OK);
    }

    @PostMapping("/agent/{agentId}/request-inspection")
    @PreAuthorize("hasRole('COLLECTION_AGENT')")
    public ResponseEntity<Void> requestInspection(@PathVariable Long agentId) {
        collectionService.requestBulkInspection(agentId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('FACILITY_MANAGER')")
    public ResponseEntity<CollectionRequest> approve(@PathVariable Long id) {
        return new ResponseEntity<>(collectionService.approveInspection(id), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        collectionService.deleteRequest(id);
        return new ResponseEntity<>("CollectionRequest deleted successfully.", HttpStatus.OK);
    }
}
