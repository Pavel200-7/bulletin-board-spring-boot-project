package com.example.bulletin.application.service.category;

import com.example.bulletin.application.exception.DuplicateResourceException;
import com.example.bulletin.application.exception.ResourceNotFoundException;
import com.example.bulletin.application.mapper.CategoryMapper;
import com.example.bulletin.application.service.category.data.request.CreateChildCategoryRequest;
import com.example.bulletin.application.service.category.data.request.CreateLeafyChildCategoryRequest;
import com.example.bulletin.application.service.category.data.request.CreateRootCategoryRequest;
import com.example.bulletin.application.service.category.data.response.CreateChildCategoryResponse;
import com.example.bulletin.application.service.category.data.response.CreateLeafyChildCategoryResponse;
import com.example.bulletin.application.service.category.data.response.CreateRootCategoryResponse;
import com.example.bulletin.application.service.category.data.response.data.CategoryResponse;
import com.example.bulletin.domain.entity.Category;
import com.example.bulletin.infrastructure.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

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

        Category root = Category.createRoot(request.getName());
        root = repository.save(root);
        CategoryResponse categoryResponse = mapper.toResponse(root);
        return new CreateRootCategoryResponse(categoryResponse);
    }

    @Override
    public CreateChildCategoryResponse createChild(CreateChildCategoryRequest request) {
        Category parent = checkParentCategory(request.getName(), request.getParentId());
        Category child = parent.createChild(request.getName());
        child = repository.save(child);
        CategoryResponse categoryResponse = mapper.toResponse(child);
        return new CreateChildCategoryResponse(categoryResponse);
    }

    @Override
    public CreateLeafyChildCategoryResponse createLeafyChild(CreateLeafyChildCategoryRequest request) {
        Category parent = checkParentCategory(request.getName(), request.getParentId());
        Category child = parent.createLeafyChild(request.getName());
        child = repository.save(child);
        CategoryResponse categoryResponse = mapper.toResponse(child);
        return new CreateLeafyChildCategoryResponse(categoryResponse);
    }

    private Category checkParentCategory(String name, UUID parentId) {
        if (repository.existsByNameAndParentId(name, parentId)) {
            throw new DuplicateResourceException("There parent category has a child category with such name.");
        }
        Category parent = repository.findById(parentId)
                .orElseThrow(() -> new ResourceNotFoundException("There is not any parent category with such id."));
        return parent;
    }

}
