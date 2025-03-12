package org.onelab.user_service.repository;

import org.onelab.user_service.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    List<UserEntity> findByUpdatedAtAfter(Instant updatedAt);
    Optional<UserEntity> findByUsername(String username);
    Optional<UserEntity> findByPhone(String phone);
}
