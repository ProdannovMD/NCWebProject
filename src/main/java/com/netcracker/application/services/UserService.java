package com.netcracker.application.services;

import com.netcracker.application.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    List<User> getAllUsers();

    User getCurrentUser();

    void createUser(User user);

    void updateUser(User user);

    void logoutUser();

    User getUserById(Long id);
}
