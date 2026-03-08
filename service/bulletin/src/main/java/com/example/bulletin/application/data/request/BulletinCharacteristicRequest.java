package com.example.bulletin.application.data.request;

import com.example.bulletin.application.data.response.CharacteristicResponse;
import com.example.bulletin.application.data.response.CharacteristicValueResponse;
import lombok.Builder;
import lombok.Value;

import java.util.Objects;
import java.util.UUID;

@Value
@Builder
public class BulletinCharacteristicRequest {
    private UUID id;
    private UUID CharacteristicId;
    private UUID CharacteristicValueId;
}
