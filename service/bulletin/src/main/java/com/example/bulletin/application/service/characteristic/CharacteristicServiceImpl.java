package com.example.bulletin.application.service.characteristic;

import com.example.bulletin.application.exception.ResourceNotFoundException;
import com.example.bulletin.application.mapper.CharacteristicMapper;
import com.example.bulletin.application.service.characteristic.data.request.*;
import com.example.bulletin.application.service.characteristic.data.response.*;
import com.example.bulletin.application.data.response.CharacteristicResponse;
import com.example.bulletin.application.service.characteristic.helper.inter.CharacteristicHierarchyPolicy;
import com.example.bulletin.domain.entity.Category;
import com.example.bulletin.domain.entity.Characteristic;
import com.example.bulletin.infrastructure.repository.CategoryRepository;
import com.example.bulletin.infrastructure.repository.CharacteristicRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CharacteristicServiceImpl implements CharacteristicService {

    private final CharacteristicRepository characteristicRepository;
    private final CategoryRepository categoryRepository;
    private final CharacteristicHierarchyPolicy hierarchyPolicy;
    private final CharacteristicMapper mapper;


    @Override
    @Transactional(readOnly = true)
    public GetCharacteristicResponse getCharacteristic(GetCharacteristicRequest request) {
        Characteristic characteristic = characteristicRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("A characteristic with this id is not found."));
        CharacteristicResponse characteristicResponse = mapper.toResponse(characteristic);
        return new GetCharacteristicResponse(characteristicResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public GetCategoryCharacteristicsResponse getCategoryCharacteristics(GetCategoryCharacteristicsRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("A category with this id is not found."));

        List<Characteristic> characteristics = characteristicRepository.findByCategoryId(request.getCategoryId());

        List<CharacteristicResponse> characteristicResponse = characteristics.stream()
                .map(c -> mapper.toResponse(c))
                .collect(Collectors.toList());
        return new GetCategoryCharacteristicsResponse(characteristicResponse);
    }

    @Override
    @Transactional
    public CreateCharacteristicResponse createCharacteristic(CreateCharacteristicRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("A category with this id is not found."));

        hierarchyPolicy.enforceAddingRules(category, request.getName());

        Characteristic characteristic = category.addCharacteristic(request.getName());
        categoryRepository.save(category);

        CharacteristicResponse characteristicResponse = mapper.toResponse(characteristic);
        return new CreateCharacteristicResponse(characteristicResponse);
    }

    @Override
    @Transactional
    public RenameCharacteristicResponse renameCharacteristic(RenameCharacteristicRequest request) {
        Characteristic characteristic = characteristicRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("A characteristic with this id is not found."));

        characteristic = characteristic.rename(request.getName());
        characteristicRepository.save(characteristic);

        CharacteristicResponse characteristicResponse = mapper.toResponse(characteristic);
        return new RenameCharacteristicResponse(characteristicResponse);
    }

    @Override
    @Transactional
    public DeleteCharacteristicResponse deleteCharacteristic(DeleteCharacteristicRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("A category with this id is not found."));
        category.removeCharacteristic(request.getCharacteristicId());
        categoryRepository.save(category);
        return new DeleteCharacteristicResponse();
    }



}
