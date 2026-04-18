package com.example.quanlybanvemaybay.repository;

import com.example.quanlybanvemaybay.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @Query("select u from User u left join fetch u.role where u.username = :username")
    Optional<User> findByUsernameWithRole(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);
}

