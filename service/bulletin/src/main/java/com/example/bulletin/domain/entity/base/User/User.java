package com.example.bulletin.domain.entity.base.User;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "town_latitude", precision = 10, scale = 8)
    private Double townLatitude;

    @Column(name = "town_longitude", precision = 11, scale = 8)
    private Double townLongitude;

    @Column(name = "is_blocked")
    private boolean isBlocked;

    @PrePersist
    public void onInit() {
        this.isBlocked = false;
    }

}
