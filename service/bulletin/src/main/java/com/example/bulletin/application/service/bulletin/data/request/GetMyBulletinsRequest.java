package com.example.bulletin.application.service.bulletin.data.request;

import com.example.bulletin.application.service.bulletin.data.request.data.PageData;
import com.example.bulletin.domain.enums.bulletin.BulletinState;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetMyBulletinsRequest {
    @Valid
    private PageData pageData;

    private BulletinState state;
    private String title;
}