package com.example.bulletin.application.service.category;

import com.example.bulletin.application.exception.DuplicateResourceException;
import com.example.bulletin.application.exception.ResourceNotFoundException;
import com.example.bulletin.application.mapper.CategoryMapper;
import com.example.bulletin.application.service.category.data.request.*;
import com.example.bulletin.application.service.category.data.response.*;
import com.example.bulletin.application.data.response.CategoryResponse;
import com.example.bulletin.application.service.category.data.response.data.*;
import com.example.bulletin.application.service.category.helper.inter.CategoryFamilyResponseBuilder;
import com.example.bulletin.domain.entity.Category;
import com.example.bulletin.infrastructure.repository.BulletinRepository;
import com.example.bulletin.infrastructure.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final BulletinRepository bulletinRepository;
    private final CategoryFamilyResponseBuilder responseBuilder;
    private final CategoryMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public GetCategoryResponse getCategory(GetCategoryRequest request) {
        Category category = categoryRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("There is not any category with such id."));
        CategoryResponse categoryResponse = mapper.toResponse(category);
        return new GetCategoryResponse(categoryResponse);
    }

    @Override
    public GetCategoryWithChildrenResponse getCategoryWithChildren(GetCategoryWithChildrenRequest request) {
        Category category = categoryRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("There is not any category with such id."));

        CategoryWithChildrenResponse categoryWithChildrenResponse = mapper.toWithChildrenResponse(category);
        return new GetCategoryWithChildrenResponse(categoryWithChildrenResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public GetCategoryWithFamilyResponse getCategoryWithFamily(GetCategoryWithFamilyRequest request) {
        Category category = categoryRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("There is not any category with such id."));
        CategoryFamilyResponse familyResponse = responseBuilder.buildResponse(category);
        return new GetCategoryWithFamilyResponse(familyResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public GetRootCategoriesResponse getRootCategories(GetRootCategoriesRequest request) {
        List<Category> categories = categoryRepository.findByParentId(null);

        List<CategoryResponse> categoryResponse = categories.stream()
                .map(c -> mapper.toResponse(c))
                .collect(Collectors.toList());
        return new GetRootCategoriesResponse(categoryResponse);
    }

    @Override
    @Transactional
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
    @Transactional
    public CreateChildCategoryResponse createChild(CreateChildCategoryRequest request) {
        Category parent = checkParentCategory(request.getName(), request.getParentId());
        Category child = parent.createChild(request.getName());
        categoryRepository.save(parent);

        CategoryResponse categoryResponse = mapper.toResponse(child);
        return new CreateChildCategoryResponse(categoryResponse);
    }

    @Override
    @Transactional
    public CreateLeafyChildCategoryResponse createLeafyChild(CreateLeafyChildCategoryRequest request) {
        Category parent = checkParentCategory(request.getName(), request.getParentId());
        Category child = parent.createLeafyChild(request.getName());
        categoryRepository.save(parent);

        CategoryResponse categoryResponse = mapper.toResponse(child);
        return new CreateLeafyChildCategoryResponse(categoryResponse);
    }

    private Category checkParentCategory(String name, UUID parentId) {
        if (categoryRepository.existsByNameAndParentId(name, parentId)) {
            throw new DuplicateResourceException("There parent category has a child category with such name.");
        }

        Category parent = categoryRepository.findById(parentId)
                .orElseThrow(() -> new ResourceNotFoundException("There is not any parent category with such id."));
        return parent;
    }

    @Override
    @Transactional
    public RenameCategoryResponse renameCategory(RenameCategoryRequest request) {
        Category category = categoryRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("There is not any category with such id."));

        category.rename(request.getName());
        category = categoryRepository.save(category);

        CategoryResponse categoryResponse = mapper.toResponse(category);
        return new RenameCategoryResponse(categoryResponse);
    }

    @Override
    @Transactional
    public DeleteChildCategoryResponse deleteChildCategory(DeleteChildCategoryRequest request) {
        Category parentCategory = categoryRepository.findById(request.getParentId())
                .orElseThrow(() -> new ResourceNotFoundException("There is not any category with such id."));
        parentCategory.removeChild(request.getChildId());
        categoryRepository.save(parentCategory);
        return new DeleteChildCategoryResponse();
    }

    @Override
    public DeleteRootCategoryResponse deleteRootCategory(DeleteRootCategoryRequest request) {
        Category rootCategory = categoryRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("There is not any category with such id."));
        rootCategory.delete();
        categoryRepository.delete(rootCategory);
        return null;
    }

}
