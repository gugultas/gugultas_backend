package com.serbest.magazine.backend.repository;

import com.serbest.magazine.backend.entity.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Transactional
public interface PlaylistRepository extends JpaRepository<Playlist, UUID> {

    List<Playlist> findAllByAuthorUsernameOrderByCreateDateTimeDesc(String username);
}
