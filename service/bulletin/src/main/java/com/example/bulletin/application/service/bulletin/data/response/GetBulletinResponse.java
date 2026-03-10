package com.example.bulletin.application.service.bulletin.data.response;

import com.example.bulletin.application.data.response.BulletinResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetBulletinResponse {
    private BulletinResponse bulletinResponse;
}
