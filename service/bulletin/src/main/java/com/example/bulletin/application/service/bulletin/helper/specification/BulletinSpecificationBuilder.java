package com.example.bulletin.application.service.bulletin.helper.specification;

import com.example.bulletin.application.service.bulletin.data.request.data.BulletinSearchCriteria;
import com.example.bulletin.domain.entity.Bulletin;
import org.springframework.data.jpa.domain.Specification;

public interface BulletinSpecificationBuilder {
    Specification<Bulletin> fromCriteria(BulletinSearchCriteria criteria);
}
