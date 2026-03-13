package com.example.chat.application.data.request.data;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageData {

    @Min(0)
    private int page;

    @Min(0)
    @Max(100)
    private int size;
}
