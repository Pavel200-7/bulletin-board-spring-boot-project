package com.example.bulletin.application.mapper;

import com.example.bulletin.application.data.response.CategoryResponse;
import com.example.bulletin.application.service.category.data.response.data.CategoryWithChildrenResponse;
import com.example.bulletin.domain.entity.Category;
import com.example.bulletin.domain.vo.CategoryData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CategoryMapper {

    @Mapping(target = "parentId", source = "parent.id")
    CategoryData toData(Category entity);

    @Mapping(target = "parentId", source = "parent.id")
    CategoryResponse toResponse(Category entity);

    @Mapping(target = "parentId", source = "parent.id")
    @Mapping(target = "children", source = "children")
    CategoryWithChildrenResponse toWithChildrenResponse(Category entity);
}
