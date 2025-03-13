package org.onelab.restaurant_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onelab.restaurant_service.entity.DishDocument;
import org.onelab.restaurant_service.entity.DishEntity;
import org.onelab.restaurant_service.entity.SyncMetadata;
import org.onelab.restaurant_service.mapper.DishMapper;
import org.onelab.restaurant_service.repository.DishElasticRepository;
import org.onelab.restaurant_service.repository.DishRepository;
import org.onelab.restaurant_service.repository.SyncMetadataRepository;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ElasticSyncService {

    private final DishRepository dishRepository;
    private final DishElasticRepository dishElasticRepository;
    private final SyncMetadataRepository syncMetadataRepository;

    @Scheduled(fixedDelay = 60000) // Запуск каждые 60 секунд
    public void syncDishes() {
        log.info("Start syncing dishes...");

        for (int attempt = 1; attempt <= 3; attempt++) {
            try {
                SyncMetadata syncMetadata = getSyncMetadata();
                List<DishEntity> newOrUpdatedDishes = dishRepository.findByUpdatedAtAfter(syncMetadata.getLastSyncTime());

                if (newOrUpdatedDishes.isEmpty()) {
                    log.info("No new or updated dishes found.");
                    return;
                }

                List<DishDocument> dishDocuments = newOrUpdatedDishes.stream()
                        .map(DishMapper::toDocument)
                        .toList();
                dishElasticRepository.saveAll(dishDocuments);

                updateSyncMetadata(syncMetadata);
                log.info("Dish synchronization finished.");
                return;

            } catch (ObjectOptimisticLockingFailureException e) {
                log.warn("Optimistic lock exception. Retrying... (attempt {}/3)", attempt);
            }
        }

        log.error("Failed to sync dishes after 3 attempts.");
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public SyncMetadata getSyncMetadata() {
        return syncMetadataRepository.findById(1L)
                .orElse(new SyncMetadata(1L, Instant.now().minus(10, ChronoUnit.MINUTES)));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateSyncMetadata(SyncMetadata syncMetadata) {
        try {
            syncMetadata.setLastSyncTime(Instant.now());
            syncMetadataRepository.saveAndFlush(syncMetadata);
        } catch (ObjectOptimisticLockingFailureException e) {
            log.warn("Optimistic lock exception while updating SyncMetadata. Skipping update.");
        }
    }
}
