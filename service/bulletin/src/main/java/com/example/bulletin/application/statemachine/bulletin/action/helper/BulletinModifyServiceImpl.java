package com.example.bulletin.application.statemachine.bulletin.action.helper;

import com.example.bulletin.application.data.request.BulletinCharacteristicRequest;
import com.example.bulletin.application.data.request.BulletinRequest;
import com.example.bulletin.application.exception.ResourceNotFoundException;
import com.example.bulletin.domain.entity.*;
import com.example.bulletin.infrastructure.repository.CategoryRepository;
import com.example.bulletin.infrastructure.repository.CharacteristicRepository;
import com.example.bulletin.infrastructure.repository.CharacteristicValueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BulletinModifyServiceImpl implements BulletinModifyService {

    private final CategoryRepository categoryRepository;
    private final CharacteristicRepository characteristicRepository;
    private final CharacteristicValueRepository characteristicValueRepository;

    public void updateBulletin(Bulletin bulletin, BulletinRequest request) {
        updateSimpleFields(bulletin, request);
        updateCategory(bulletin, request);
        updateCharacteristics(bulletin, request.getCharacteristics());
    }

    private void updateSimpleFields(Bulletin bulletin, BulletinRequest request) {
        bulletin.setTitle(request.getTitle());
        bulletin.setDescription(request.getDescription());
        bulletin.setPrice(request.getPrice());
    }

    private void updateCategory(Bulletin bulletin, BulletinRequest request) {
        Optional<UUID> newCategoryId = Optional.ofNullable(request.getCategoryId());
        if (newCategoryId.isEmpty()) {
            return;
        }
        if (bulletin.getCategory() != null && bulletin.getCategory().getId().equals(newCategoryId.get())) {
            return;
        }

        Category newCategory = categoryRepository.findById(newCategoryId.get())
                .orElseThrow(() -> new ResourceNotFoundException("Category is not found. id: " + newCategoryId.get()));
        bulletin.setCategory(newCategory);
    }

    private void updateCharacteristics(Bulletin bulletin,
            List<BulletinCharacteristicRequest> requestCharacteristics) {
        if (requestCharacteristics == null) {
            return;
        }

        Map<UUID, BulletinCharacteristic> existingCharacteristics = bulletin.getCharacteristics()
                .stream()
                .collect(Collectors.toMap(
                        bc -> bc.getName().getId(),
                        bc -> bc));

        Set<UUID> requestedCharacteristicIds = requestCharacteristics.stream()
                .map(BulletinCharacteristicRequest::getCharacteristicId)
                .collect(Collectors.toSet());

        removeMissingCharacteristics(bulletin, existingCharacteristics, requestedCharacteristicIds);

        for (BulletinCharacteristicRequest requestCharacteristic : requestCharacteristics) {
            updateOrAddCharacteristic(bulletin, existingCharacteristics, requestCharacteristic);
        }
    }

    private void removeMissingCharacteristics(Bulletin bulletin,
                                              Map<UUID, BulletinCharacteristic> existingCharacteristics,
                                              Set<UUID> requestedCharacteristicIds) {
        existingCharacteristics.values().stream()
                .filter(bc -> !requestedCharacteristicIds.contains(bc.getName().getId()))
                .forEach(bc -> bulletin.removeCharacteristic(bc.getId()));
    }

    private void updateOrAddCharacteristic(Bulletin bulletin,
                                           Map<UUID, BulletinCharacteristic> existingCharacteristics,
                                           BulletinCharacteristicRequest requestCharacteristic) {
        UUID characteristicId = requestCharacteristic.getCharacteristicId();
        UUID valueId = requestCharacteristic.getCharacteristicValueId();

        BulletinCharacteristic existingCharacteristic = existingCharacteristics.get(characteristicId);
        if (existingCharacteristic != null) {
            updateExistingCharacteristic(existingCharacteristic, valueId);
        } else {
            addNewCharacteristic(bulletin, characteristicId, valueId);
        }
    }

    private void updateExistingCharacteristic(BulletinCharacteristic bulletinCharacteristic,
                                              UUID newValueId) {
        Optional<UUID> oldValueId = Optional.ofNullable(bulletinCharacteristic.getValue().getId());
        if (oldValueId.isPresent()) {
            return;
        }
        if (oldValueId.get().equals(newValueId)) {
            return;
        }

        CharacteristicValue newValue = characteristicValueRepository.findById(newValueId)
                .orElseThrow(() -> new ResourceNotFoundException("Characteristic value not found. id:" + newValueId));
        bulletinCharacteristic.setValue(newValue);
    }

    private void addNewCharacteristic(Bulletin bulletin, UUID characteristicId, UUID valueId) {
        Characteristic characteristic = characteristicRepository.findById(characteristicId)
                .orElseThrow(() -> new ResourceNotFoundException("Characteristic not found. id:" + characteristicId));
        BulletinCharacteristic newCharacteristic = bulletin.addCharacteristic(characteristic);

        CharacteristicValue value = characteristicValueRepository.findById(valueId)
                .orElseThrow(() -> new ResourceNotFoundException("Characteristic value not found. id: " + valueId));
        newCharacteristic.setValue(value);
    }
}
