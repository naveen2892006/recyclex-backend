package com.example.demo.service;

import com.example.demo.entity.RecyclingFacility;
import com.example.demo.repository.RecyclingFacilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FacilityService {

    private final RecyclingFacilityRepository repository;

    public List<RecyclingFacility> getAllFacilities() {
        return repository.findAll();
    }

    public RecyclingFacility getFacilityStats(Long id) {
        return repository.findById(id).orElseThrow();
    }

    public RecyclingFacility saveFacility(RecyclingFacility facility) {
        if (facility.getCurrentOccupancyKg() == null) {
            facility.setCurrentOccupancyKg(0.0);
        }
        return repository.save(facility);
    }
}
