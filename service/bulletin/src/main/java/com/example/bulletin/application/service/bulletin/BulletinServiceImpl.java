package com.example.bulletin.application.service.bulletin;

import com.example.bulletin.application.mapper.BulletinMapper;
import com.example.bulletin.application.service.bulletin.data.request.CreateBulletinRequest;
import com.example.bulletin.application.service.bulletin.data.response.CreateBulletinResponse;
import com.example.bulletin.domain.entity.Bulletin;
import com.example.bulletin.domain.entity.base.OwnerInfo;
import com.example.bulletin.domain.entity.base.user.User;
import com.example.bulletin.infrastructure.repository.BulletinRepository;
import com.example.bulletin.infrastructure.repository.UserRepository;
import com.example.bulletin.infrastructure.security.SecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BulletinServiceImpl implements BulletinService {

    private final BulletinRepository bulletinRepository;
    private final UserRepository userRepository;
    private final SecurityService securityService;
    private final BulletinMapper mapper;

    @Override
    public CreateBulletinResponse createBulletin(CreateBulletinRequest request) {
        UUID userId = securityService.getCurrentUserIdAsUUID();
        Optional<User> user = userRepository.findById(userId);
        OwnerInfo ownerInfo = new OwnerInfo(user.get());

         Bulletin bulletin = Bulletin.createDraft(ownerInfo);
        bulletinRepository.save(bulletin);

        return null;
    }

}
