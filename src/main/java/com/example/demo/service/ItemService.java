package com.example.demo.service;

import com.example.demo.entity.RecyclableItem;
import com.example.demo.repository.RecyclableItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final RecyclableItemRepository repository;

    public RecyclableItem updateItemWeight(Long itemId, Double weight, String grade) {
        RecyclableItem item = repository.findById(itemId).orElseThrow();
        item.setActualWeight(weight);
        item.setQualityGrade(grade);
        return repository.save(item);
    }

    public List<RecyclableItem> getItemsByBatch(Long batchId) {
        return repository.findByBatchId(batchId);
    }
}
