package com.serbest.magazine.backend.repository;

import com.serbest.magazine.backend.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, UUID> {
    VerificationToken findByToken(String token);
}
