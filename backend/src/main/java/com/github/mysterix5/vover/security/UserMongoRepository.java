package com.github.mysterix5.vover.security;

import com.github.mysterix5.vover.model.security.VoverUserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserMongoRepository extends MongoRepository<VoverUserEntity, String> {
    Optional<VoverUserEntity> findByUsername(String username);
    boolean existsByUsername(String username);

    boolean existsByUsernameIgnoreCase(String username);
}