package com.fisor.mvp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fisor.mvp.model.User;

public interface UserRepository extends JpaRepository <User, Long>{
    Optional<User> findByUsernameOrEmail(String username, String email);
}


