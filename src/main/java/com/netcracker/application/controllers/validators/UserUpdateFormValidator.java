package com.netcracker.application.controllers.validators;

import com.netcracker.application.controllers.forms.UserUpdateForm;
import com.netcracker.application.model.User;
import com.netcracker.application.repository.UserRepository;
import com.netcracker.application.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Locale;
import java.util.Objects;

@Component
public class UserUpdateFormValidator implements Validator, MessageSourceAware {

    private final UserRepository userRepository;
    private final UserService userService;

    private MessageSource messageSource;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserUpdateFormValidator(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return UserUpdateForm.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        UserUpdateForm form = (UserUpdateForm) target;

        User currentUser = userService.getCurrentUser();
        if (!passwordEncoder.matches(form.getOldPassword(), currentUser.getPassword())) {
            errors.rejectValue(
                    "oldPassword", "auth.password.wrong",
                    messageSource.getMessage("auth.password.wrong", null, Locale.getDefault())
            );
            return;
        }

        if (!form.getName().equals(currentUser.getUsername())) {
            User byName = userRepository.findByName(form.getName());
            if (Objects.nonNull(byName) && !byName.getId().equals(currentUser.getId()))
                errors.rejectValue(
                        "name", "registration.username.non-unique",
                        messageSource.getMessage("registration.username.non-unique", null, Locale.getDefault())
                );
        }

        if (!form.getPassword().equals(form.getConfirmPassword()))
            errors.rejectValue(
                    "password", "registration.password.no-match",
                    messageSource.getMessage("registration.password.no-match", null, Locale.getDefault())
            );


    }
}
