package com.example.socialnetwork.common.configuration;

import java.util.HashSet;
import java.util.Set;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.socialnetwork.module.identity.entity.Permission;
import com.example.socialnetwork.module.identity.entity.Role;
import com.example.socialnetwork.module.identity.repository.PermissionRepository;
import com.example.socialnetwork.module.identity.repository.RoleRepository;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class ApplicationInit {

    @Bean
    ApplicationRunner init(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        return args -> {
            if (roleRepository.findById("ADMIN").isPresent()) {
                return;
            }
            Permission approvePost = Permission.builder().name("APPROVE_POST").build();
            permissionRepository.save(approvePost);

            Permission rejectPost = Permission.builder().name("REJECT_POST").build();
            permissionRepository.save(rejectPost);

            Role adminRole = Role.builder()
                    .name("ADMIN")
                    .permissions(Set.of(approvePost, rejectPost))
                    .build();
            roleRepository.save(adminRole);

            Role userRole = Role.builder()
                    .name("USER")
                    .permissions(new HashSet<>())
                    .build();
            roleRepository.save(userRole);

            log.info("Initialized roles and permissions");
        };
    }
}