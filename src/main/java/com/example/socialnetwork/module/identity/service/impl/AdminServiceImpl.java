package com.example.socialnetwork.module.identity.service.impl;

import com.example.socialnetwork.common.exception.AppException;
import com.example.socialnetwork.common.exception.ErrorCode;
import com.example.socialnetwork.module.identity.dto.request.AssignRoleToUserRequest;
import com.example.socialnetwork.module.identity.dto.request.BanRequest;
import com.example.socialnetwork.module.identity.dto.response.UserPrivateResponse;
import com.example.socialnetwork.module.identity.entity.Role;
import com.example.socialnetwork.module.identity.entity.User;
import com.example.socialnetwork.module.identity.mapper.UserMapper;
import com.example.socialnetwork.module.identity.repository.RoleRepository;
import com.example.socialnetwork.module.identity.repository.UserRepository;
import com.example.socialnetwork.module.identity.service.AdminService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminServiceImpl implements AdminService {
    UserRepository userRepository;
    UserMapper userMapper;
    RoleRepository roleRepository;

    @Override
    public List<UserPrivateResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(userMapper::toUserPrivateResponse).toList();
    }

    @Override
    @Transactional
    public UserPrivateResponse deleteUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new AppException(ErrorCode.USER_NOT_FOUND));
        user.setDeleted(true);
        user.setDeleteAt(LocalDateTime.now());
        return userMapper.toUserPrivateResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserPrivateResponse banUser(BanRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(()-> new AppException(ErrorCode.USER_NOT_FOUND));
        user.setBanned(true);
        user.setBanAt(LocalDateTime.now());
        user.setBanReason(request.getReason());
        return userMapper.toUserPrivateResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserPrivateResponse assignRoleToUser(AssignRoleToUserRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(()-> new AppException(ErrorCode.USER_NOT_FOUND));
        List<Role> roles = roleRepository.findAllById(request.getRoles());
        user.setRoles(new HashSet<>(roles));
        User savedUser = userRepository.save(user);
        return userMapper.toUserPrivateResponse(savedUser);
    }
}
