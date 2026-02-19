package com.example.bulletin.application.service.characteristic.helper.impl;

import com.example.bulletin.application.service.characteristic.helper.inter.CharacteristicHierarchyPolicy;
import com.example.bulletin.domain.entity.Category;
import com.example.bulletin.infrastructure.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CharacteristicHierarchyPolicyImpl implements CharacteristicHierarchyPolicy {

    private final CategoryRepository repository;

    @Override
    public void enforceAddingRules(Category category, String characteristicName)
            throws IllegalStateException {
        if (repository.existsCharacteristicWithNameInHierarchy(category.getId(), characteristicName)) {
            throw new IllegalStateException("Characteristic with this name is already exists in hierarchy.");
        }
    }
}
