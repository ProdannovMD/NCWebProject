package com.netcracker.application.services.impl;

import com.netcracker.application.controllers.exceptions.ResourceNotFoundException;
import com.netcracker.application.model.User;
import com.netcracker.application.repository.UserRepository;
import com.netcracker.application.services.UserService;
import com.netcracker.application.services.UsersTaskService;
import com.netcracker.logging.LogManager;
import com.netcracker.logging.loggers.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service("userDetailsService")
public class UserServiceImpl implements UserService {
    private final Logger logger = LogManager.getLogger("main.java", UserService.class);
    private final UserRepository userRepository;
    private final UsersTaskService usersTaskService;

    @Autowired
    public UserServiceImpl(
            UserRepository userRepository,
            UsersTaskService usersTaskService
    ) {
        this.userRepository = userRepository;
        this.usersTaskService = usersTaskService;
    }

    public void createUser(User user) {
        userRepository.save(user);
        logger.info("New user created: " + user);
        usersTaskService.addDefaultTaskForUser(user);
        logger.info("For user " + user + " added default task");
    }

    @Override
    public void updateUser(User user) {
        userRepository.save(user);
        logger.info("User " + user + " has been updated");
    }

    @Override
    public void logoutUser() {
        SecurityContextHolder.clearContext();
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getCurrentUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        User currentUser = null;
        if (userDetails instanceof User) {
            currentUser = (User) userDetails;
        }
        return currentUser;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User foundUser = userRepository.findByName(username);
        if (Objects.isNull(foundUser))
            throw new UsernameNotFoundException("Unable to find user with username " + username);
        return foundUser;
    }
}
