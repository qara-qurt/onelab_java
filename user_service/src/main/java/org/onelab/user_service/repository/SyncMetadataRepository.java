package org.onelab.user_service.repository;

import jakarta.persistence.LockModeType;
import org.onelab.user_service.entity.SyncMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SyncMetadataRepository extends JpaRepository<SyncMetadata, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM SyncMetadata s WHERE s.id = :id")
    SyncMetadata findByIdWithLock(@Param("id") Long id);
}
