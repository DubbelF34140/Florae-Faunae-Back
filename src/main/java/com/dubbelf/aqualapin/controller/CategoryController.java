package com.dubbelf.aqualapin.controller;

import com.dubbelf.aqualapin.dto.CategoryStatsDTO;
import com.dubbelf.aqualapin.dto.CreateCategoryDTO;
import com.dubbelf.aqualapin.entity.Category;
import com.dubbelf.aqualapin.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/categories")
@CrossOrigin(
        origins = {"http://localhost:5173","http://localhost", "https://localhost", "capacitor://localhost" },
        allowCredentials = "true"
)
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Category> createCategory(@RequestBody CreateCategoryDTO dto) {
        return ResponseEntity.ok(
                categoryService.createCategory(
                        dto.getName(),
                        dto.getDescription(),
                        dto.getSlug(),
                        dto.getColor(),
                        dto.getIcon()
                )
        );
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/update/{catId}")
    public ResponseEntity<Void> UpdateCategory(@PathVariable UUID catId, @RequestBody CreateCategoryDTO dto) {
        categoryService.edit(catId, dto);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{catId}")
    public ResponseEntity<Void> DeleteCategory(@PathVariable UUID catId) {
        categoryService.delete(catId);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }


    @GetMapping("/best")
    public ResponseEntity<List<CategoryStatsDTO>> getBestCategories() {
        return ResponseEntity.ok(categoryService.getBestCategories());
    }
}
