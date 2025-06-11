package com.pixalive.backend.service;

import com.pixalive.backend.dto.UserDto;
import com.pixalive.backend.model.User;

import java.util.List;

public interface UserService {
    User createUser(UserDto userDto);
    List<User> getAllUsers();
    User getUserById(Long id);
    User updateUsers(Long id, UserDto userDto);
    void deleteUser(Long id);

}
