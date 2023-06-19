package com.serbest.magazine.backend.repository;

import com.serbest.magazine.backend.common.repository.MasterpieceRepository;
import com.serbest.magazine.backend.entity.Picture;
import org.springframework.stereotype.Repository;

@Repository
public interface PictureRepository extends MasterpieceRepository<Picture> {
}
