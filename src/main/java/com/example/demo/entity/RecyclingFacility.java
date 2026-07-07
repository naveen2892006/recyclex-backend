package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "recycling_facilities")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class RecyclingFacility {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String location;

    @Column(name = "max_capacity_kg", nullable = false)
    private Double maxCapacityKg;

    @Builder.Default
    @Column(name = "current_occupancy_kg")
    private Double currentOccupancyKg = 0.0;

    @ManyToOne
    @JoinColumn(name = "manager_id")
    private SystemUser manager;
}
