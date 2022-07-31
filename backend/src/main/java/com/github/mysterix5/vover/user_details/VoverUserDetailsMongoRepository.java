package com.github.mysterix5.vover.user_details;

import com.github.mysterix5.vover.model.user_details.VoverUserDetails;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoverUserDetailsMongoRepository extends MongoRepository<VoverUserDetails, String> {

}
