package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "system_users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class SystemUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Builder.Default
    private Double rewardPoints = 0.0;

    @Builder.Default
    private Double organicWaste = 0.0;

    @Builder.Default
    private Double recyclableWaste = 0.0;

    @Builder.Default
    private Double nonRecyclableWaste = 0.0;

    @Builder.Default
    private Double hazardousWaste = 0.0;

    @Builder.Default
    private Double collectedWasteBalance = 0.0;

    @Builder.Default
    private Double totalContributedKg = 0.0;

    @Builder.Default
    private Double pendingWasteKg = 0.0;

    public enum UserRole {
        RECYCLING_COORDINATOR,
        FACILITY_MANAGER,
        COLLECTION_AGENT,
        CONTRIBUTOR, ADMIN
    }
}
