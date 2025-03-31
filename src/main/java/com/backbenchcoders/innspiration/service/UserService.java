package com.backbenchcoders.innspiration.service;

import com.backbenchcoders.innspiration.dto.ProfileUpdateRequestDto;
import com.backbenchcoders.innspiration.dto.UserDto;
import com.backbenchcoders.innspiration.entity.User;

public interface UserService {
    User getUserById(Long id);

    void updateProfile(ProfileUpdateRequestDto profileUpdateRequestDto);

    UserDto getMyProfile();
}
