package com.example.bulletin.application.data.request;

import com.example.bulletin.application.data.response.BulletinCharacteristicResponse;
import com.example.bulletin.application.data.response.BulletinImageResponse;
import com.example.bulletin.application.data.response.CategoryResponse;
import com.example.bulletin.domain.enums.bulletin.BulletinState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.IntStream;

@Value
@Builder
@AllArgsConstructor
public class BulletinRequest {
    private UUID id;
    private String title;
    private String description;
    private double price;
    private UUID categoryId;
    private List<BulletinCharacteristicRequest> characteristics;
}
