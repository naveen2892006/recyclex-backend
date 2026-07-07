package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponseDto {
    private String token;
    private String username;
    private String role;
    private Long id;
    private Double rewardPoints;
    private Double organicWaste;
    private Double recyclableWaste;
    private Double nonRecyclableWaste;
    private Double hazardousWaste;
    private Double collectedWasteBalance;
    private Double totalContributedKg;
    private Double pendingWasteKg;
}
