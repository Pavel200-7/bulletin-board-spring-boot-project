package com.example.chat.application.service.profile.data.request.data;

import com.example.chat.application.service.profile.data.request.data.enums.ProfileOrderBy;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Sort.Direction;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileSearchCriteria {
    private String publicName;
    private ProfileOrderBy orderBy;
    private Direction direction;
}
