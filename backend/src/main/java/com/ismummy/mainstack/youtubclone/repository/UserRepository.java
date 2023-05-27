package com.ismummy.mainstack.youtubclone.repository;

import com.ismummy.mainstack.youtubclone.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findBySub(String sub);
}
