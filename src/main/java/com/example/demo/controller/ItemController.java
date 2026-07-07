package com.example.demo.controller;

import com.example.demo.entity.RecyclableItem;
import com.example.demo.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PutMapping("/{id}/weight")
    @PreAuthorize("hasRole('COLLECTION_AGENT')")
    public ResponseEntity<RecyclableItem> updateWeight(@PathVariable Long id,
                                                         @RequestParam Double weight,
                                                         @RequestParam String grade) {
        return new ResponseEntity<>(itemService.updateItemWeight(id, weight, grade), HttpStatus.OK);
    }
}
