package com.netcracker.application.controllers.forms.converters;

import com.netcracker.application.controllers.forms.UserRegistrationForm;
import com.netcracker.application.model.Role;
import com.netcracker.application.model.User;
import com.netcracker.application.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class UserConverter implements Converter<UserRegistrationForm, User> {
    private static final long USER_ROLE_ID = 1L;

    private PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Autowired
    public UserConverter(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User convert(UserRegistrationForm source) {
        User user = new User();
        user.setName(source.getName());
        user.setPassword(passwordEncoder.encode(source.getPassword()));
        Role role = roleRepository.findById(USER_ROLE_ID).orElseThrow(IllegalStateException::new);
        user.setRoles(Collections.singleton(role));
        return user;
    }
}
