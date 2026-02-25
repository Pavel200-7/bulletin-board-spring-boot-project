package com.example.bulletin.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

    @OneToMany(mappedBy = "parent",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Category> children = new ArrayList<>();

    @OneToMany(mappedBy = "category",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Characteristic> characteristics = new ArrayList<>();

    protected Category() {}

    private Category(String name, Category parent, boolean isLeaf) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.parent = parent;
        this.leaf = isLeaf;
    }

    public static Category createRoot(String name) {
        return new Category(name, null, false);
    }

    public Category createChild(String name) {
        if (this.leaf) {
            throw new IllegalStateException("Cannot add child to leaf category.");
        }
        Category child = new Category(name, this, false);
        this.children.add(child);
        return child;
    }

    public Category createLeafyChild(String name) {
        if (this.leaf) {
            throw new IllegalStateException("Cannot add child to leaf category.");
        }
        Category child = new Category(name, this, true);
        this.children.add(child);
        return child;
    }

    public Category rename(String newName) {
        this.name = newName;
        return this;
    }

    public Category removeChild(UUID deletingId) {
        Category child = findCategory(deletingId)
                .orElseThrow(() -> new IllegalStateException("This is not category of the parent."));
        child.delete();
        return this;
    }

    private void delete() {
        if (!this.children.isEmpty()) {
            throw new IllegalStateException("This category has children and can not be deleted.");
        }
        parent.removeChild(this);
    }

    private void removeChild(Category child) {
        if (child.parent != this) {
            throw new IllegalStateException("Child not found");
        }
        this.children.remove(child);
        child.parent = null;
    }

    private Optional<Category> findCategory(UUID id) {
        return children.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst();
    }

    public Characteristic addCharacteristic(String name) {
        Characteristic characteristic = Characteristic.createCharacteristic(name, this);
        this.characteristics.add(characteristic);
        return characteristic;
    }

    public Category removeCharacteristic(UUID removingId) {
        Characteristic characteristic = findCharacteristic(removingId)
                .orElseThrow(() -> new IllegalStateException("This is not characteristic value of existing characteristic."));
        this.characteristics.remove(characteristic);
        return this;
    }

    private Optional<Characteristic> findCharacteristic(UUID id) {
        return characteristics.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst();
    }

}
