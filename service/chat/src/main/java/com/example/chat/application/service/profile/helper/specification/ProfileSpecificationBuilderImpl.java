package com.example.chat.application.service.profile.helper.specification;

import com.example.chat.application.service.profile.data.request.data.ProfileSearchCriteria;
import com.example.chat.domain.entity.Profile;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class ProfileSpecificationBuilderImpl {

    public Specification<Profile> fromCriteria(ProfileSearchCriteria criteria) {
        if (criteria == null) {
            return Specification.where((Specification<Profile>) null);
        }
        return Specification.where(publicNameContains(criteria.getPublicName()));
    }

    private Specification<Profile> publicNameContains(String publicName) {
        return (root, query, cb) -> {
            if (!StringUtils.hasText(publicName)) {
                return cb.conjunction();
            }
            return cb.like(cb.lower(root.get("publicName")),
                    "%" + publicName.toLowerCase() + "%");
        };
    }
}