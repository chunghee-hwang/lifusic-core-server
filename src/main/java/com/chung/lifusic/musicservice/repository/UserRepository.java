package com.chung.lifusic.musicservice.repository;

import com.chung.lifusic.musicservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String mail);
}
