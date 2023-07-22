package com.serbest.magazine.backend.repository;

import com.serbest.magazine.backend.entity.Contact;
import com.serbest.magazine.backend.entity.EncyclopediaArticle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Transactional
public interface EncyclopediaArticleRepository extends JpaRepository<EncyclopediaArticle,UUID> {

    List<EncyclopediaArticle> findAllByOrderByCreateDateTimeDesc();

    List<EncyclopediaArticle> findTop7ByOrderByCreateDateTimeDesc();
}
