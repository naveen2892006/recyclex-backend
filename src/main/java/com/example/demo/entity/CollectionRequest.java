package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "collection_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class CollectionRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "contributor_id", nullable = false)
    private SystemUser contributor;

    @ManyToOne
    @JoinColumn(name = "agent_id")
    private SystemUser agent;

    @Column(name = "pickup_address", nullable = false)
    private String pickupAddress;

    @Column(name = "scheduled_date", nullable = false)
    @FutureOrPresent(message = "Pickup date cannot be in the past")
    private LocalDate scheduledDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(100)")
    private RequestStatus status;

    private Double totalWeightEstimate;

    @Builder.Default
    @Column(name = "reward_points_earned")
    private Double rewardPointsEarned = 0.0;

    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL)
    @JsonManagedReference(value = "request-items")
    private List<RecyclableItem> items;

    public enum RequestStatus {
        SUBMITTED,
        ASSIGNED,
        COLLECTED,
        UNDER_INSPECTION,
        APPROVED,
        CANCELLED
    }
}
