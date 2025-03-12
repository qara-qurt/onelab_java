package org.onelab.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onelab.user_service.entity.SyncMetadata;
import org.onelab.user_service.entity.UserDocument;
import org.onelab.user_service.entity.UserEntity;
import org.onelab.user_service.mapper.UserMapper;
import org.onelab.user_service.repository.SyncMetadataRepository;
import org.onelab.user_service.repository.UserElasticRepository;
import org.onelab.user_service.repository.UserRepository;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserSyncService {

    private final UserRepository userRepository;
    private final UserElasticRepository userElasticRepository;
    private final SyncMetadataRepository syncMetadataRepository;

    @Scheduled(fixedDelay = 60000)
    public void syncUsers() {
        log.info("Start sync users...");

        for (int attempt = 1; attempt <= 3; attempt++) {
            try {
                SyncMetadata syncMetadata = getSyncMetadata();
                List<UserEntity> newOrUpdatedUsers = userRepository.findByUpdatedAtAfter(syncMetadata.getLastSyncTime());

                if (newOrUpdatedUsers.isEmpty()) {
                    log.info("No new or updated users found.");
                    return;
                }

                List<UserDocument> userDocuments = newOrUpdatedUsers.stream()
                        .map(UserMapper::toDocument)
                        .toList();
                userElasticRepository.saveAll(userDocuments);

                updateSyncMetadata(syncMetadata);
                log.info("Synchronization finished.");
                return;

            } catch (ObjectOptimisticLockingFailureException e) {
                log.warn("Optimistic lock exception. Retrying... (attempt {}/3)", attempt);
            }
        }

        log.error("Failed to sync users after 3 attempts.");
    }

    private SyncMetadata getSyncMetadata() {
        try {
            return syncMetadataRepository.findByIdWithLock(1L);
        } catch (Exception e) {
            log.warn("TransactionRequiredException: No active transaction. Using default SyncMetadata.");
            return syncMetadataRepository.findById(1L)
                    .orElse(new SyncMetadata(1L, Instant.now().minus(10, ChronoUnit.MINUTES)));
        }
    }

    private void updateSyncMetadata(SyncMetadata syncMetadata) {
        try {
            syncMetadata.setLastSyncTime(Instant.now());
            syncMetadataRepository.saveAndFlush(syncMetadata);
        } catch (ObjectOptimisticLockingFailureException e) {
            log.warn("Optimistic lock exception while updating SyncMetadata. Skipping update.");
        }
    }
}
