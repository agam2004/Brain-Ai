package com.example.brainAi.repository;

import com.example.brainAi.entity.RSAKeysEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RSAKeysRepository  extends JpaRepository<RSAKeysEntity, Integer> {
    RSAKeysEntity findTopByOrderByKidDesc();
}
