package com.example.demo.repository;

import com.example.demo.entity.RecyclingFacility;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecyclingFacilityRepository extends JpaRepository<RecyclingFacility, Long> {
    List<RecyclingFacility> findByCurrentOccupancyKgLessThan(Double limit);
}
