package com.example.bulletin.application.service.category.data.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeleteLeafCategoryResponse {
    private boolean succeed = true;
}
