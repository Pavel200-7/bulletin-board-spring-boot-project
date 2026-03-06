package com.example.bulletin.application.service.bulletin;

import com.example.bulletin.application.mapper.BulletinMapper;
import com.example.bulletin.application.service.bulletin.data.request.CreateBulletinRequest;
import com.example.bulletin.application.service.bulletin.data.response.CreateBulletinResponse;
import com.example.bulletin.domain.entity.Bulletin;
import com.example.bulletin.domain.entity.base.OwnerInfo;
import com.example.bulletin.domain.entity.base.user.User;
import com.example.bulletin.infrastructure.repository.BulletinRepository;
import com.example.bulletin.infrastructure.repository.UserRepository;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BulletinServiceImpl implements BulletinService {

    private final BulletinRepository repository;
    private final UserRepository userRepository;
    private final BulletinMapper mapper;

    @Override
    public CreateBulletinResponse createBulletin(CreateBulletinRequest request) {
        // Bulletin bulletin = Bulletin.createDraft()

        return null;
    }

}
