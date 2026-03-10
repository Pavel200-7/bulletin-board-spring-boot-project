package com.example.bulletin.application.service.bulletin.data.response.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BulletinPaginationData {
    private UUID id;
    private String title;
    private UUID image;
    private double price;
}
