package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "recyclable_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecyclableItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "request_id", nullable = false)
    @JsonBackReference(value = "request-items")
    private CollectionRequest request;

    @Enumerated(EnumType.STRING)
    @Column(name = "material_type", nullable = false)
    private MaterialType materialType;

    @Column(name = "actual_weight")
    private Double actualWeight;

    @Column(name = "quality_grade")
    private String qualityGrade;

    @ManyToOne
    @JoinColumn(name = "facility_id")
    private RecyclingFacility facility;

   
    @ManyToOne
    @JoinColumn(name = "batch_id")
    @JsonBackReference(value = "batch-items")
    private ProcessingBatch batch;

    public enum MaterialType {
        ORGANIC_WET(5),
        RECYCLABLE_DRY(10),
        NON_RECYCLABLE(2),
        HAZARDOUS_SPECIAL(20);

        private final int pointsPerKg;

        MaterialType(int pointsPerKg) {
            this.pointsPerKg = pointsPerKg;
        }

        public int getPointsPerKg() {
            return pointsPerKg;
        }
    }
}