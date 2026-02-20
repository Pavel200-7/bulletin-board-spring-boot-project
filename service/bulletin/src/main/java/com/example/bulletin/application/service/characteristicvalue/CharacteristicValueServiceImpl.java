package com.example.bulletin.application.service.characteristicvalue;

import com.example.bulletin.application.exception.ResourceNotFoundException;
import com.example.bulletin.application.mapper.CharacteristicValueMapper;
import com.example.bulletin.application.service.characteristicvalue.data.request.*;
import com.example.bulletin.application.service.characteristicvalue.data.response.*;
import com.example.bulletin.application.service.characteristicvalue.data.response.data.CharacteristicValueResponse;
import com.example.bulletin.domain.entity.Characteristic;
import com.example.bulletin.domain.entity.CharacteristicValue;
import com.example.bulletin.infrastructure.repository.CharacteristicRepository;
import com.example.bulletin.infrastructure.repository.CharacteristicValueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CharacteristicValueServiceImpl implements CharacteristicValueService {

    private final CharacteristicValueRepository characteristicValueRepository;
    private final CharacteristicRepository characteristicRepository;
    private final CharacteristicValueMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public GetCharacteristicValueResponse getCharacteristicValue(GetCharacteristicValueRequest request) {
        CharacteristicValue characteristicValue = characteristicValueRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("A characteristic value with this id is not found."));
        CharacteristicValueResponse characteristicValueResponse = mapper.toResponse(characteristicValue);
        return new GetCharacteristicValueResponse(characteristicValueResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public GetCharacteristicValuesResponse getCharacteristicValues(GetCharacteristicValuesRequest request) {
        Characteristic characteristic = characteristicRepository.findById(request.getCharacteristicId())
                .orElseThrow(() -> new ResourceNotFoundException("A characteristic with this id is not found."));

        List<CharacteristicValue> characteristicValues = characteristicValueRepository.findByCharacteristicId(request.getCharacteristicId());

        List<CharacteristicValueResponse> characteristicValueResponses = characteristicValues.stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
        return new GetCharacteristicValuesResponse(characteristicValueResponses);
    }

    @Override
    @Transactional
    public CreateCharacteristicValueResponse createCharacteristicValue(CreateCharacteristicValueRequest request) {
        Characteristic characteristic = characteristicRepository.findById(request.getCharacteristicId())
                .orElseThrow(() -> new ResourceNotFoundException("A characteristic with this id is not found."));

        CharacteristicValue characteristicValue = characteristic.addPossibleValue(request.getName());
        characteristicValueRepository.save(characteristicValue);

        CharacteristicValueResponse characteristicValueResponse = mapper.toResponse(characteristicValue);
        return new CreateCharacteristicValueResponse(characteristicValueResponse);
    }

    @Override
    @Transactional
    public RenameCharacteristicValueResponse renameCharacteristicValue(RenameCharacteristicValueRequest request) {
        CharacteristicValue characteristicValue = characteristicValueRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("A characteristic value with this id is not found."));

        characteristicValue = characteristicValue.rename(request.getName());
        characteristicValueRepository.save(characteristicValue);

        CharacteristicValueResponse characteristicValueResponse = mapper.toResponse(characteristicValue);
        return new RenameCharacteristicValueResponse(characteristicValueResponse);
    }

    @Override
    @Transactional
    public DeleteCharacteristicValueResponse deleteCharacteristicValue(DeleteCharacteristicValueRequest request) {
        CharacteristicValue characteristicValue = characteristicValueRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("A characteristic value with this id is not found."));

        characteristicValueRepository.delete(characteristicValue);
        return new DeleteCharacteristicValueResponse();
    }

}