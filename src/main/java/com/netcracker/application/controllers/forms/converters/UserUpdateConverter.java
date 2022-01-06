package com.netcracker.application.controllers.forms.converters;

import com.netcracker.application.controllers.forms.UserUpdateForm;
import com.netcracker.application.model.User;
import com.netcracker.application.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserUpdateConverter implements Converter<UserUpdateForm, User> {

    private final UserService userService;

    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserUpdateConverter(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User convert(UserUpdateForm source) {
        User currentUser = userService.getCurrentUser();
        currentUser.setName(source.getName());
        if (source.getPassword().length() > 0) {
            currentUser.setPassword(
                    passwordEncoder.encode(source.getPassword())
            );
        }
        return currentUser;
    }
}
