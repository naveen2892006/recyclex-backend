package com.example.demo.controller;

import com.example.demo.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('RECYCLING_COORDINATOR', 'FACILITY_MANAGER')")
    public ResponseEntity<Map<String, Object>> getStats() {
        return new ResponseEntity<>(adminService.getGlobalStats(), HttpStatus.OK);
    }
}
