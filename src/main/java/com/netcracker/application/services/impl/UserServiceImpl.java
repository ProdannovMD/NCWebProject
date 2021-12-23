package com.netcracker.application.services.impl;

import com.netcracker.application.controllers.forms.UserRegistrationForm;
import com.netcracker.application.model.Role;
import com.netcracker.application.model.User;
import com.netcracker.application.repository.RoleRepository;
import com.netcracker.application.repository.UserRepository;
import com.netcracker.application.services.UserService;
import com.netcracker.application.services.UsersTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service("userDetailsService")
public class UserServiceImpl implements UserService {
    private static final long USER_ROLE_ID = 1L;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UsersTaskService usersTaskService;
    private  PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(
            UserRepository userRepository,
            RoleRepository roleRepository,
            UsersTaskService usersTaskService
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.usersTaskService = usersTaskService;
    }

    @Autowired
    public void setPasswordEncoder(@Lazy PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public void createUser(UserRegistrationForm form) {
        User user = new User();
        user.setName(form.getName());
        user.setPassword(passwordEncoder.encode(form.getPassword()));
        Role role = roleRepository.findById(USER_ROLE_ID).orElseThrow(IllegalStateException::new);
        user.setRoles(Collections.singleton(role));
        userRepository.save(user);

        usersTaskService.addDefaultTaskForUser(user);

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
