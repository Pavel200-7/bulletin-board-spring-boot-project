package com.example.bulletin.unit.application.service.category.helper.impl;

import com.example.bulletin.application.service.category.data.response.data.CategoryFamilyResponse;
import com.example.bulletin.application.service.category.helper.impl.CategoryFamilyResponseBuilderImpl;
import com.example.bulletin.domain.entity.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ActiveProfiles("test")
public class CategoryFamilyResponseBuilderTests {

    private CategoryFamilyResponseBuilderImpl responseBuilder;

    @BeforeEach
    public void setUp() {
        responseBuilder = new CategoryFamilyResponseBuilderImpl();
    }

    @Test
    public void shouldCreateCorrectResponse() {
        // Arrange
        Category category = createCategoryWithFamily();
        CategoryFamilyResponse expected = createResponse(category);

        // Act
        CategoryFamilyResponse actual = responseBuilder.buildResponse(category);

        // Assert
        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }


    // root level 1 (УРОВЕНЬ 1 - КОРЕНЬ)
    //│
    //├── child 1 level 2 (УРОВЕНЬ 2)
    //│   │
    //│   ├── child 1 level 3 (УРОВЕНЬ 3)
    //│   │   │
    //│   │   ├── child 1 level 4 (УРОВЕНЬ 4) ← ЦЕЛЕВАЯ КАТЕГОРИЯ ⭐
    //│   │   │   │
    //│   │   │   ├── child 1 level 5 leaf (УРОВЕНЬ 5 - ЛИСТ)
    //│   │   │   ├── child 2 level 5 leaf (УРОВЕНЬ 5 - ЛИСТ)
    //│   │   │   └── child 3 level 5 leaf (УРОВЕНЬ 5 - ЛИСТ)
    //│   │   │
    //│   │   ├── child 2 level 4 (УРОВЕНЬ 4)
    //│   │   └── child 3 level 4 (УРОВЕНЬ 4)
    //│   │
    //│   ├── child 2 level 3 (УРОВЕНЬ 3)
    //│   └── child 3 level 3 (УРОВЕНЬ 3)
    //│
    //├── child 2 level 2 (УРОВЕНЬ 2)
    //│   │
    //│   ├── child 4 level 3 (УРОВЕНЬ 3)
    //│   ├── child 5 level 3 (УРОВЕНЬ 3)
    //│   └── child 6 level 3 (УРОВЕНЬ 3)
    //│
    //└── child 3 level 2 (УРОВЕНЬ 2)
    //    │
    //    ├── child 7 level 3 (УРОВЕНЬ 3)
    //    ├── child 8 level 3 (УРОВЕНЬ 3)
    //    └── child 9 level 3 (УРОВЕНЬ 3)
    public Category createCategoryWithFamily() {
        Category root = Category.createRoot("root level 1");

        Category category1Level2 = root.createChild("child 1 level 2");
        Category category2Level2 = root.createChild("child 2 level 2");
        Category category3Level2 = root.createChild("child 3 level 2");

        Category category1Level3 = category1Level2.createChild("child 1 level 3");
        Category category2Level3 = category1Level2.createChild("child 2 level 3");
        Category category3Level3 = category1Level2.createChild("child 3 level 3");

        Category category4Level3 = category2Level2.createChild("child 4 level 3");
        Category category5Level3 = category2Level2.createChild("child 5 level 3");
        Category category6Level3 = category2Level2.createChild("child 6 level 3");

        Category category7Level3 = category3Level2.createChild("child 7 level 3");
        Category category8Level3 = category3Level2.createChild("child 8 level 3");
        Category category9Level3 = category3Level2.createChild("child 9 level 3");

        Category category1Level4 = category1Level3.createChild("child 1 level 4");
        Category category2Level4 = category1Level3.createChild("child 2 level 4");
        Category category3Level4 = category1Level3.createChild("child 3 level 4");

        Category category1Level5Leaf = category1Level4.createLeafyChild("child 1 level 5 leaf");
        Category category2Level5Leaf = category1Level4.createLeafyChild("child 2 level 5 leaf");
        Category category3Level5Leaf = category1Level4.createLeafyChild("child 3 level 5 leaf");

        return category1Level4;
    }

    //    root level 1 (УРОВЕНЬ 1)
    //│
    //├── child 1 level 2 (УРОВЕНЬ 2 - ПУТЬ К TARGET) ★
    //│   │
    //│   ├── child 1 level 3 (УРОВЕНЬ 3 - ПУТЬ К TARGET) ★
    //│   │   │
    //│   │   ├── child 1 level 4 (УРОВЕНЬ 4 - TARGET) ⭐
    //│   │   │   │
    //│   │   │   ├── child 1 level 5 leaf (ЛИСТ)
    //│   │   │   ├── child 2 level 5 leaf (ЛИСТ)
    //│   │   │   └── child 3 level 5 leaf (ЛИСТ)
    //│   │   │
    //│   │   ├── child 2 level 4 (БОКОВАЯ ВЕТКА - без детей)
    //│   │   └── child 3 level 4 (БОКОВАЯ ВЕТКА - без детей)
    //│   │
    //│   ├── child 2 level 3 (БОКОВАЯ ВЕТКА - без детей)
    //│   └── child 3 level 3 (БОКОВАЯ ВЕТКА - без детей)
    //│
    //├── child 2 level 2 (БОКОВАЯ ВЕТКА - без детей)
    //│
    //└── child 3 level 2 (БОКОВАЯ ВЕТКА - без детей)
    public CategoryFamilyResponse createResponse(Category category) {
        Category current = category;
        while (current.getParent() != null) {
            current = current.getParent();
        }
        Category root = current;  // root level 1

        CategoryFamilyResponse rootResponse = mapToCategoryFamilyResponse(root);

        for (Category level2Category : root.getChildren()) {
            CategoryFamilyResponse level2Response = mapToCategoryFamilyResponse(level2Category);

            if (isAncestorOf(level2Category, category)) {

                for (Category level3Category : level2Category.getChildren()) {
                    CategoryFamilyResponse level3Response = mapToCategoryFamilyResponse(level3Category);

                    if (isAncestorOf(level3Category, category)) {

                        for (Category level4Category : level3Category.getChildren()) {
                            CategoryFamilyResponse level4Response = mapToCategoryFamilyResponse(level4Category);

                            if (level4Category.getId().equals(category.getId())) {

                                for (Category leafCategory : level4Category.getChildren()) {
                                    CategoryFamilyResponse leafResponse = mapToCategoryFamilyResponse(leafCategory);
                                    level4Response.getChildren().add(leafResponse);
                                }

                                level3Response.getChildren().add(level4Response);

                            } else {
                                level3Response.getChildren().add(level4Response);
                            }
                        }

                        level2Response.getChildren().add(level3Response);
                    } else {
                        level2Response.getChildren().add(level3Response);
                    }
                }
            }
            rootResponse.getChildren().add(level2Response);
        }

        return rootResponse; // Эх, за sanctum TTD-sicus
    }

    // Проверяет, является ли potentialAncestor предком category
    private boolean isAncestorOf(Category potentialAncestor, Category category) {
        Category current = category;
        while (current.getParent() != null) {
            if (current.getParent().getId().equals(potentialAncestor.getId())) {
                return true;
            }
            current = current.getParent();
        }
        return false;
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
