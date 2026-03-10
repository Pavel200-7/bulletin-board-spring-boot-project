package com.example.bulletin.domain.entity.base.user;

import com.example.bulletin.domain.entity.TradeAccount;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

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

    @OneToOne(mappedBy = "ownerInfo.owner", cascade = CascadeType.ALL)
    private TradeAccount tradeAccount;

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
