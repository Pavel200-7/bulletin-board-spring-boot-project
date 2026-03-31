package com.example.chat.infrastructure.repository;

import com.example.chat.domain.entity.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface ContactRepository extends JpaRepository<Contact, UUID> {
    boolean existsByOwnerProfileIdAndContactProfileId(UUID ownerProfileId, UUID contactProfileId);

    @Query("SELECT c FROM Contact c WHERE c.ownerProfile.id = :profileId OR c.contactProfile.id = :profileId")
    List<Contact> findByOwnerProfileId(@Param("profileId") UUID ownerProfileId);

    @Query("SELECT c FROM Contact c WHERE (c.ownerProfile.id = :profileId1 AND c.contactProfile.id = :profileId2) " +
            "OR (c.ownerProfile.id = :profileId2 AND c.contactProfile.id = :profileId1)")
    Optional<Contact> findByProfilesId(@Param("profileId1") UUID profileId1,
                                       @Param("profileId2") UUID profileId2);

    @Query(value = """
            SELECT contact_profile_id FROM contact WHERE owner_profile_id = :profileId
            UNION
            SELECT owner_profile_id FROM contact WHERE contact_profile_id = :profileId
            """, nativeQuery = true)
    Set<UUID> findContactProfileIdsByOwnerProfileId(@Param("profileId") UUID profileId);
}
