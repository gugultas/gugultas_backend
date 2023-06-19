package com.serbest.magazine.backend.common.repository;

import com.serbest.magazine.backend.common.entity.Masterpiece;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MasterpieceRepository<I extends Masterpiece> extends JpaRepository<I, UUID> {

    I findTopByOrderByCreateDateTimeDesc();
}
