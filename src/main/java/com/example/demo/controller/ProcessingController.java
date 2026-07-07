package com.example.demo.controller;

import com.example.demo.dto.BatchCreationDto;
import com.example.demo.entity.ProcessingBatch;
import com.example.demo.entity.RecyclableItem;
import com.example.demo.service.ProcessingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/processing")
@RequiredArgsConstructor
public class ProcessingController {

    private final ProcessingService processingService;

    @PostMapping("/batch")
    @PreAuthorize("hasRole('FACILITY_MANAGER')")
    public ResponseEntity<ProcessingBatch> createBatch(@RequestBody BatchCreationDto dto) {
        return new ResponseEntity<>(processingService.createBatch(dto.getFacilityId(), dto.getItemIds()), HttpStatus.OK);
    }

    @PutMapping("/batch/{id}/complete")
    @PreAuthorize("hasRole('FACILITY_MANAGER')")
    public ResponseEntity<ProcessingBatch> completeBatch(@PathVariable Long id) {
        return new ResponseEntity<>(processingService.completeBatch(id), HttpStatus.OK);
    }

    @GetMapping("/batches")
    @PreAuthorize("hasRole('FACILITY_MANAGER')")
    public ResponseEntity<List<ProcessingBatch>> getAllBatches() {
        return new ResponseEntity<>(processingService.getAllBatches(), HttpStatus.OK);
    }

    @GetMapping("/available-items")
    @PreAuthorize("hasRole('FACILITY_MANAGER')")
    public ResponseEntity<List<RecyclableItem>> getAvailableItems() {
        return new ResponseEntity<>(processingService.getAvailableItems(), HttpStatus.OK);
    }

    @GetMapping("/all-items")
    @PreAuthorize("hasRole('FACILITY_MANAGER')")
    public ResponseEntity<List<RecyclableItem>> getAllItems() {
        return new ResponseEntity<>(processingService.getAllItemsForProcessing(), HttpStatus.OK);
    }
}
