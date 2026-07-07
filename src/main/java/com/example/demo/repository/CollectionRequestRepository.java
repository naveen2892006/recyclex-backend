package com.example.demo.repository;

import com.example.demo.entity.CollectionRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CollectionRequestRepository extends JpaRepository<CollectionRequest, Long> {

    List<CollectionRequest> findByAgentIdAndStatus(Long agentId, CollectionRequest.RequestStatus status);

    List<CollectionRequest> findByContributorId(Long contributorId);

    List<CollectionRequest> findByStatus(CollectionRequest.RequestStatus status);

    @Query("SELECT c FROM CollectionRequest c WHERE c.status = :status")
    List<CollectionRequest> findByStatusCustom(@Param("status") CollectionRequest.RequestStatus status);
}
