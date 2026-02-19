package com.example.bulletin.application.service.characteristic.helper.inter;

import com.example.bulletin.domain.entity.Category;

public interface CharacteristicHierarchyPolicy  {
    public void enforceAddingRules(Category category, String characteristicName);
}
