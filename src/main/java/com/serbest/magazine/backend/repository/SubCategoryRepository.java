package com.serbest.magazine.backend.repository;

import com.serbest.magazine.backend.entity.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SubCategoryRepository extends JpaRepository<SubCategory, UUID> {

    List<SubCategory> findAllByCategoryName(String categoryName);
    List<SubCategory> findAllByCategoryNameAndActiveTrue(String categoryName);
    Optional<SubCategory> findByName(String category);
}
