package com.example.chat.application.service.profile.helper.specification;

import com.example.chat.application.service.profile.data.request.data.ProfileSearchCriteria;
import com.example.chat.domain.entity.Profile;
import org.springframework.data.jpa.domain.Specification;

public interface ProfileSpecificationBuilder {
    Specification<Profile> fromCriteria(ProfileSearchCriteria criteria);
}
