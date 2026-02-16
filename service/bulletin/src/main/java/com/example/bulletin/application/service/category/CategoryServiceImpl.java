package com.example.bulletin.application.service.category;

import com.example.bulletin.application.exception.DuplicateResourceException;
import com.example.bulletin.application.exception.ResourceNotFoundException;
import com.example.bulletin.application.mapper.CategoryMapper;
import com.example.bulletin.application.service.category.data.request.*;
import com.example.bulletin.application.service.category.data.response.*;
import com.example.bulletin.application.service.category.data.response.data.CategoryFamilyResponse;
import com.example.bulletin.application.service.category.data.response.data.CategoryResponse;
import com.example.bulletin.application.service.category.helper.inter.CategoryFamilyResponseBuilder;
import com.example.bulletin.domain.entity.Category;
import com.example.bulletin.infrastructure.repository.BulletinRepository;
import com.example.bulletin.infrastructure.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final BulletinRepository bulletinRepository;
    private final CategoryFamilyResponseBuilder responseBuilder;
    private final CategoryMapper mapper;

    @Override
    public GetCategoryResponse getCategory(GetCategoryRequest request) {
        Category category = categoryRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("There is not any category with such id."));
        CategoryResponse categoryResponse = mapper.toResponse(category);
        return new GetCategoryResponse(categoryResponse);
    }

    @Override
    public GetCategoryWithFamilyResponse getCategoryWithFamily(GetCategoryWithFamilyRequest request) {
        Category category = categoryRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("There is not any category with such id."));
        CategoryFamilyResponse familyResponse = responseBuilder.buildResponse(category);
        return new GetCategoryWithFamilyResponse(familyResponse);
    }

    @Override
    public CreateRootCategoryResponse createRoot(CreateRootCategoryRequest request) {
        if (categoryRepository.existsByNameAndParentId(request.getName(), null)) {
            throw new DuplicateResourceException("There is a root category with such name.");
        }

        Category root = Category.createRoot(request.getName());
        root = categoryRepository.save(root);
        CategoryResponse categoryResponse = mapper.toResponse(root);
        return new CreateRootCategoryResponse(categoryResponse);
    }

    @Override
    public CreateChildCategoryResponse createChild(CreateChildCategoryRequest request) {
        Category parent = checkParentCategory(request.getName(), request.getParentId());
        Category child = parent.createChild(request.getName());
        child = categoryRepository.save(child);
        CategoryResponse categoryResponse = mapper.toResponse(child);
        return new CreateChildCategoryResponse(categoryResponse);
    }

    @Override
    public CreateLeafyChildCategoryResponse createLeafyChild(CreateLeafyChildCategoryRequest request) {
        Category parent = checkParentCategory(request.getName(), request.getParentId());
        Category child = parent.createLeafyChild(request.getName());
        child = categoryRepository.save(child);
        CategoryResponse categoryResponse = mapper.toResponse(child);
        return new CreateLeafyChildCategoryResponse(categoryResponse);
    }

    @Override
    public RenameCategoryResponse renameCategory(RenameCategoryRequest request) {
        Category category = categoryRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("There is not any category with such id."));

        category.rename(request.getName());
        category = categoryRepository.save(category);

        CategoryResponse categoryResponse = mapper.toResponse(category);
        return new RenameCategoryResponse(categoryResponse);
    }

    @Override
    public DeleteCategoryResponse deleteCategory(DeleteCategoryRequest request) {
        Category category = categoryRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("There is not any category with such id."));

        category.delete();
        categoryRepository.delete(category);
        return new DeleteCategoryResponse();
    }

    @Override
    public DeleteLeafCategoryResponse deleteLeafCategory(DeleteLeafCategoryRequest request) {
        Category category = categoryRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("There is not any category with such id."));
        if (bulletinRepository.existsByCategoryId(request.getId())) {
            throw new IllegalStateException("There are bulletins described with this category.");
        }

        category.deleteLeaf();
        categoryRepository.delete(category);
        return new DeleteLeafCategoryResponse();
    }

    private Category checkParentCategory(String name, UUID parentId) {
        if (categoryRepository.existsByNameAndParentId(name, parentId)) {
            throw new DuplicateResourceException("There parent category has a child category with such name.");
        }
        Category parent = categoryRepository.findById(parentId)
                .orElseThrow(() -> new ResourceNotFoundException("There is not any parent category with such id."));
        return parent;
    }

}
