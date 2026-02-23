package com.example.bulletin.domain.entity;

import jakarta.persistence.*;

import lombok.Getter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Table(name = "characteristics")
public class Characteristic {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @OneToMany(mappedBy = "characteristic",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<CharacteristicValue> possibleValues = new ArrayList<>();

    protected Characteristic() {}

    private Characteristic(String name, Category category) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.category = category;
    }

    public static Characteristic createCharacteristic(String name, Category category) {
        return new Characteristic(name, category);
    }

    public Characteristic rename(String newName) {
        this.name = newName;
        return this;
    }

    public CharacteristicValue addPossibleValue(String valueName) {
        CharacteristicValue value = CharacteristicValue.createCharacteristicValue(valueName, this);
        this.possibleValues.add(value);
        return value;
    }

    public void removePossibleValue(CharacteristicValue value) {
        if (!value.getCharacteristic().getId()
                .equals(this.getId())) {
            throw new IllegalStateException("This is not characteristic value of existing characteristic.");
        }
        this.possibleValues.remove(value);
    }

}
