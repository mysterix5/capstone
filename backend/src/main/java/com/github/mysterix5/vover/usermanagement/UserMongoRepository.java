package com.github.mysterix5.vover.usermanagement;

import com.github.mysterix5.vover.model.VoverUser;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserMongoRepository extends MongoRepository<VoverUser, String> {
    Optional<VoverUser> findByUsername(String username);
    boolean existsByUsername(String username);
}