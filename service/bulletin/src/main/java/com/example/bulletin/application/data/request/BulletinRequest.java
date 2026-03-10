package com.example.bulletin.application.data.request;

import com.example.bulletin.application.data.response.BulletinCharacteristicResponse;
import com.example.bulletin.application.data.response.BulletinImageResponse;
import com.example.bulletin.application.data.response.CategoryResponse;
import com.example.bulletin.domain.enums.bulletin.BulletinState;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.IntStream;

@Value
@Builder
@ToString
@AllArgsConstructor
public class BulletinRequest {

    @NotNull(message = "ID must not be null")
    private UUID id;

    @NotBlank(message = "Title must not be blank")
    private String title;

    @NotBlank(message = "Description must not be blank")
    private String description;

    @NotNull(message = "Price must not be null")
    @Positive(message = "Price must be positive")
    private Double price;

    @NotNull(message = "Category ID must not be null")
    private UUID categoryId;

    @Valid
    private List<@Valid BulletinCharacteristicRequest> characteristics;
}
