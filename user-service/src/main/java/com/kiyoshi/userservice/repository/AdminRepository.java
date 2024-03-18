package com.kiyoshi.userservice.repository;

import com.kiyoshi.userservice.entity.collection.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends MongoRepository<User, String> {
    @Query("{email: ?0, role: ADMIN}")
    Optional<User> findAdminByEmail(String email);
}
