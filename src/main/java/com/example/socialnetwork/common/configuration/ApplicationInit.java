package com.example.socialnetwork.common.configuration;

import java.util.Set;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.socialnetwork.module.identity.entity.Permission;
import com.example.socialnetwork.module.identity.entity.Role;
import com.example.socialnetwork.module.identity.entity.User;
import com.example.socialnetwork.module.identity.repository.PermissionRepository;
import com.example.socialnetwork.module.identity.repository.RoleRepository;
import com.example.socialnetwork.module.identity.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApplicationInit {

    PermissionRepository permissionRepository;
    RoleRepository roleRepository;
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;

    @Bean
    ApplicationRunner init() {
        return args -> {
            Permission userRead = initPermission("USER_READ");
            Permission userReadAll = initPermission("USER_READ_ALL");
            Permission userUpdate = initPermission("USER_UPDATE");
            Permission userUpdateAny = initPermission("USER_UPDATE_ANY");
            Permission userDeleteAny = initPermission("USER_DELETE_ANY");

            Permission roleRead = initPermission("ROLE_READ");
            Permission roleWrite = initPermission("ROLE_WRITE");
            Permission permissionRead = initPermission("PERMISSION_READ");
            Permission permissionWrite = initPermission("PERMISSION_WRITE");

            // TODO: Permission for post service

            initRole(Set.of(userRead, userUpdate), "USER");
            Role adminRole = initRole(
                    Set.of(userRead, userReadAll, userUpdate, userUpdateAny, userDeleteAny,
                            roleRead, roleWrite, permissionRead, permissionWrite),
                    "ADMIN");

            if (!userRepository.existsByUsername("admin")) {
                User adminUser = User.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("Hmt2k62006!"))
                        .roles(Set.of(adminRole))
                        .build();
                userRepository.save(adminUser);
                log.info("Created default admin user");
            }

            log.info("Initialized roles and permissions");
        };
    }

    private Permission initPermission(String permissionName) {
        return permissionRepository.findById(permissionName)
                .orElseGet(() -> {
                    Permission permission = Permission.builder().name(permissionName).build();
                    return permissionRepository.save(permission);
                });
    }

    private Role initRole(Set<Permission> permissions, String roleName) {
        return roleRepository.findById(roleName)
                .orElseGet(() -> {
                    Role role = Role.builder()
                            .name(roleName)
                            .permissions(permissions)
                            .build();
                    return roleRepository.save(role);
                });
    }
}