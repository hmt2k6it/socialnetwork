package com.example.socialnetwork.module.identity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.socialnetwork.module.identity.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {

}
