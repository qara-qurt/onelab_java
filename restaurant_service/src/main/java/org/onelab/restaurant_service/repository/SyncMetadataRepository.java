package org.onelab.restaurant_service.repository;

import org.onelab.restaurant_service.entity.SyncMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SyncMetadataRepository extends JpaRepository<SyncMetadata, Long> {
}
