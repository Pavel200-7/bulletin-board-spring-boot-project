package com.example.bulletin.application.service.bulletin.data.response;

import com.example.bulletin.application.service.bulletin.data.response.data.BulletinPaginationData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetMyBulletinsResponse {
    private Page<BulletinPaginationData> page;
}