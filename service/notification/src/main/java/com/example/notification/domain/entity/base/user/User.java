package com.example.notification.domain.entity.base.user;

import com.example.notification.domain.entity.Subscription;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "users")
public class User {

    @Id
    @Column(name = "id")
    @Setter(AccessLevel.NONE)
    private UUID id;

    @Column(name = "email", nullable = false, unique = true)
    @Setter(AccessLevel.NONE)
    private String email;

    @Column(name = "blocked")
    private boolean blocked;

    @PrePersist
    public void onInit() {
        this.blocked = false;
    }

    protected User() {}

    private User(UUID id, String email) {
        this.id = id;
        this.email = email;
    }

    public static User createUser(UUID id, String email) {
        return new User(id, email);
    }

}
