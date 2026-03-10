package com.example.bulletin.application.service.bulletin.data.request;

import com.example.bulletin.application.service.bulletin.data.request.data.BulletinSearchCriteria;
import com.example.bulletin.application.service.bulletin.data.request.data.PageData;
import jakarta.validation.Valid;
import lombok.*;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class GetBulletinPaginationRequest {
    @Valid
    private PageData pageData;
    private BulletinSearchCriteria criteria;
}
