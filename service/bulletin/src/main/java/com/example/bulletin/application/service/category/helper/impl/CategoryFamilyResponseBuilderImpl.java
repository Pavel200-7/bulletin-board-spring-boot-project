package com.example.bulletin.application.service.category.helper.impl;

import com.example.bulletin.application.service.category.data.response.data.CategoryFamilyResponse;
import com.example.bulletin.application.service.category.helper.inter.CategoryFamilyResponseBuilder;
import com.example.bulletin.domain.entity.Category;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class CategoryFamilyResponseBuilderImpl implements CategoryFamilyResponseBuilder {

    @Override
    public CategoryFamilyResponse buildResponse(Category targetCategory) {
        CategoryFamilyResponse targetSubtree = buildCompleteSubtree(targetCategory);
        return buildUpwardHierarchy(targetCategory, targetSubtree);
    }

    private CategoryFamilyResponse buildCompleteSubtree(Category category) {
        CategoryFamilyResponse response = mapToCategoryFamilyResponse(category);
        List<CategoryFamilyResponse> children = category.getChildren().stream()
                .map(this::mapToCategoryFamilyResponse)
                .collect(Collectors.toList());
        response.getChildren().addAll(children);
        return response;
    }

    private CategoryFamilyResponse buildUpwardHierarchy(Category category, CategoryFamilyResponse currentSubtree) {
        Category currentCategory = category;
        CategoryFamilyResponse currentResponse = currentSubtree;

        while (currentCategory.getParent() != null) {
            Category parent = currentCategory.getParent();
            CategoryFamilyResponse parentResponse = mapToCategoryFamilyResponse(parent);

            parentResponse.getChildren().add(currentResponse);

            UUID currentCategoryId = currentCategory.getId();
            List<CategoryFamilyResponse> siblingResponses = parent.getChildren().stream()
                    .filter(sibling -> !sibling.getId().equals(currentCategoryId))
                    .map(sibling -> {
                        CategoryFamilyResponse siblingResponse = mapToCategoryFamilyResponse(sibling);
                        return siblingResponse;
                    })
                    .collect(Collectors.toList());
            parentResponse.getChildren().addAll(siblingResponses);

            currentCategory = parent;
            currentResponse = parentResponse;
        }

        return currentResponse;
    }

    public CategoryFamilyResponse mapToCategoryFamilyResponse(Category category) {
        return CategoryFamilyResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .leaf(category.isLeaf())
                .parentId(category.getParent() != null ? category.getParent().getId() : null)
                .children(new ArrayList<>())
                .build();
    }
}
