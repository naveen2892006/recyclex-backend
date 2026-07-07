package com.example.demo.controller;

import com.example.demo.entity.RecyclingFacility;
import com.example.demo.service.FacilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/facilities")
@RequiredArgsConstructor
public class FacilityController {

    private final FacilityService facilityService;

    @GetMapping
    public ResponseEntity<List<RecyclingFacility>> getAll() {
        return new ResponseEntity<>(facilityService.getAllFacilities(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecyclingFacility> getById(@PathVariable Long id) {
        return new ResponseEntity<>(facilityService.getFacilityStats(id), HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasRole('FACILITY_MANAGER')")
    public ResponseEntity<RecyclingFacility> create(@RequestBody RecyclingFacility facility) {
        return new ResponseEntity<>(facilityService.saveFacility(facility), HttpStatus.OK);
    }
}
