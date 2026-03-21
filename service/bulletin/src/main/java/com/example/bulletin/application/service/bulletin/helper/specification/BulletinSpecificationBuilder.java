package com.example.bulletin.application.service.bulletin.helper.specification;

import com.example.bulletin.application.service.bulletin.data.request.data.BulletinSearchCriteria;
import com.example.bulletin.domain.entity.Bulletin;
import com.example.bulletin.domain.enums.bulletin.BulletinState;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public interface BulletinSpecificationBuilder {
    Specification<Bulletin> fromCriteria(BulletinSearchCriteria criteria);
    Specification<Bulletin> forCurrentUser(UUID ownerId, BulletinState state, String title);
}
