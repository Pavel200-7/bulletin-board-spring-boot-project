package com.example.bulletin.infrastructure.repository;

import com.example.bulletin.domain.entity.CharacteristicValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CharacteristicValueRepository extends JpaRepository<CharacteristicValue, UUID> {
    List<CharacteristicValue> findByCharacteristicId(UUID characteristicId);
}
