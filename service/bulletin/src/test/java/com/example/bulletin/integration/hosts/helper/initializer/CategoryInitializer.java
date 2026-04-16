package com.example.bulletin.integration.hosts.helper.initializer;

import com.example.bulletin.domain.entity.Category;
import com.example.bulletin.infrastructure.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryInitializer {

    private final CategoryRepository categoryRepository;

    public Category createRoot(String name) {
        Category root = Category.createRoot(name);
        categoryRepository.save(root);
        return root;
    }

    public Category createLeafCategory() {
        Category root = createRoot("root");
        Category leaf = root.createLeafyChild("leaf");
        categoryRepository.save(root);
        return leaf;
    }

    public Category createNotRootCategoryWithLeafyChildren() {
        Category root = createRoot("root");
        Category category = root.createChild("category");
        categoryRepository.save(category);

        category.createLeafyChild("child 1");
        category.createLeafyChild("child 2");
        category.createLeafyChild("child 3");
        categoryRepository.save(category);

        return category;
    }
}
