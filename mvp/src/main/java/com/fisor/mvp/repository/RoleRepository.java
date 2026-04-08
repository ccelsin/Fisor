package com.fisor.mvp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fisor.mvp.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long>{
    Role findByName (String name);
}
