package com.example.socialnetwork.module.identity.service;

import com.example.socialnetwork.module.identity.dto.request.UserUpdateRequest;
import com.example.socialnetwork.module.identity.dto.response.UserPrivateResponse;
import com.example.socialnetwork.module.identity.dto.response.UserPublicResponse;

public interface UserService {
    UserPrivateResponse getMyProfile();

    UserPrivateResponse updateMyProfile(UserUpdateRequest userUpdateRequest);

    UserPublicResponse getUserProfile(String userId);
}
