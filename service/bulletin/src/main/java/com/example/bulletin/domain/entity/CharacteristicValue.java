package com.example.bulletin.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Table(name = "characteristic_values")
public class CharacteristicValue {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "characteristic_id", nullable = false)
    private Characteristic characteristic;

    protected CharacteristicValue() {}

    private CharacteristicValue(String name, Characteristic characteristic) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.characteristic = characteristic;
    }

    public static CharacteristicValue createCharacteristicValue(String name, Characteristic characteristic) {
        return new CharacteristicValue(name, characteristic);
    }

    public CharacteristicValue rename(String newName) {
        this.name = newName;
        return this;
    }

}
