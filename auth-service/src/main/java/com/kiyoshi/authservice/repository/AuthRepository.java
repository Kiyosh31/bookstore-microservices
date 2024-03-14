package com.kiyoshi.authservice.repository;

import com.kiyoshi.authservice.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthRepository extends MongoRepository<User, String> {
    @Query("{email: ?0, isActive: true}")
    Optional<User> findUserByEmail(String email);
}
