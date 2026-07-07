package com.example.demo.dto;

import lombok.Data;

import java.util.List;

@Data
public class BatchCreationDto {
    private Long facilityId;
    private List<Long> itemIds;
}
