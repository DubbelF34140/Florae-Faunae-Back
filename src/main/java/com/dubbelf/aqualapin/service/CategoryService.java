package com.dubbelf.aqualapin.service;

import com.dubbelf.aqualapin.dto.CreateCategoryDTO;
import com.dubbelf.aqualapin.dto.CategoryStatsDTO;
import com.dubbelf.aqualapin.entity.Category;
import com.dubbelf.aqualapin.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public Category createCategory(String name, String description, String slug, String color, String icon) {
        Category category = new Category();
        category.setName(name);
        category.setDescription(description);
        category.setSlug(slug);
        category.setColor(color);
        category.setIcon(icon);
        return categoryRepository.save(category);
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public void edit(UUID categoryID, CreateCategoryDTO dto) {
        Category category = categoryRepository.findObjetById(categoryID);
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        category.setSlug(dto.getSlug());
        category.setColor(dto.getColor());
        category.setIcon(dto.getIcon());
        categoryRepository.save(category);
    }

    public void delete(UUID catId) {
        categoryRepository.deleteById(catId);
    }

    @Transactional()
    public List<CategoryStatsDTO> getBestCategories() {
        Pageable top3 = PageRequest.of(0, 3);
        return categoryRepository.findBestCategories(top3);
    }

}
