package com.example.socialnetwork.module.identity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.socialnetwork.module.identity.entity.Permission;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, String> {

}
