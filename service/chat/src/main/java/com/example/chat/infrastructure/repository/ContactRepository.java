package com.example.chat.infrastructure.repository;

import com.example.chat.domain.entity.Contact;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ContactRepository extends JpaRepository<Contact, UUID> {
    boolean existsByOwnerProfileIdAndContactProfileId(UUID ownerProfileId, UUID contactProfileId);
    List<Contact> findByOwnerProfileId(UUID ownerProfileId);
}
