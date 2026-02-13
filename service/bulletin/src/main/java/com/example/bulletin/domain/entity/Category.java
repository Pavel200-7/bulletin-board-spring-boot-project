package com.example.bulletin.domain.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Table(name = "categories")
public class Category {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "leaf", nullable = false)
    private boolean leaf;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Category> children = new ArrayList<>();

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Characteristic> characteristics = new ArrayList<>();

    protected Category() {}

    private Category(String name, Category parent, boolean isLeaf) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.parent = parent;
        this.leaf = isLeaf;
    }

    // CREATE OPERATIONS

    public static Category createRoot(String name) {
        return new Category(name, null, false);
    }

    public Category createChild(String name) {
        if (this.leaf) {
            throw new IllegalStateException("Cannot add child to leaf category");
        }
        Category child = new Category(name, this, false);
        this.children.add(child);
        return child;
    }

    public Category createLeafyChild(String name) {
        if (this.leaf) {
            throw new IllegalStateException("Cannot add child to leaf category");
        }
        Category child = new Category(name, this, true);
        this.children.add(child);
        return child;
    }

}
