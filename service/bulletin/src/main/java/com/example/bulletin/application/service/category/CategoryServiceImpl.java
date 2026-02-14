package com.example.bulletin.application.service.category;

import com.example.bulletin.application.exception.DuplicateResourceException;
import com.example.bulletin.application.mapper.CategoryMapper;
import com.example.bulletin.application.service.category.data.request.CreateRootCategoryRequest;
import com.example.bulletin.application.service.category.data.response.CreateRootCategoryResponse;
import com.example.bulletin.application.service.category.data.response.data.CategoryResponse;
import com.example.bulletin.domain.entity.Category;
import com.example.bulletin.infrastructure.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository repository;
    private final CategoryMapper mapper;

    @Override
    public CreateRootCategoryResponse createRoot(CreateRootCategoryRequest request) {
        if (repository.existsByNameAndParentId(request.getName(), null)) {
            throw new DuplicateResourceException("There is a root category with such name.");
        }

        Category category = Category.createRoot(request.getName());
        category = repository.save(category);
        CategoryResponse categoryResponse = mapper.toResponse(category);
        return new CreateRootCategoryResponse(categoryResponse);
    }
}
