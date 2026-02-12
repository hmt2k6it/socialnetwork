package com.example.socialnetwork.module.identity.repository;

import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.socialnetwork.module.identity.entity.Role;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {
    @Override
    @EntityGraph(attributePaths = {"permissions"})
    @NonNull
    List<Role> findAll();
}
