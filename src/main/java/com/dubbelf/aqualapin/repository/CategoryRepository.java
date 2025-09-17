package com.dubbelf.aqualapin.repository;

import com.dubbelf.aqualapin.dto.CategoryStatsDTO;
import com.dubbelf.aqualapin.entity.Category;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {

    Category findObjetById(UUID categoryid);

    @Query("""
    SELECT new com.dubbelf.aqualapin.dto.CategoryStatsDTO(
        c.name,
        c.description,
        c.color,
        c.icon,
        COUNT(DISTINCT p),
        COUNT(DISTINCT cm)
    )
    FROM Category c
    LEFT JOIN c.posts p
    LEFT JOIN p.comments cm
    GROUP BY c.id, c.name, c.description
    ORDER BY COUNT(DISTINCT p) DESC, COUNT(DISTINCT cm) DESC
""")
    List<CategoryStatsDTO> findBestCategories(Pageable pageable);


}
