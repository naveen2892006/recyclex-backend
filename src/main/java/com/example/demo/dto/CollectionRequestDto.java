package com.example.demo.dto;

import com.example.demo.entity.RecyclableItem;
import lombok.Data;

import java.util.List;

@Data
public class CollectionRequestDto {
    private String pickupAddress;
    private String scheduledDate;
    private Double totalWeightEstimate;
    private List<RequestItemDto> items;

    @Data
    public static class RequestItemDto {
        private RecyclableItem.MaterialType materialType;
        private Double actualWeight;
    }
}
