package com.netcracker.application.controllers.validators;

import com.netcracker.application.controllers.forms.UserRegistrationForm;
import com.netcracker.application.model.User;
import com.netcracker.application.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Objects;

@Component
public class UserRegistrationValidator implements Validator {

    private final UserRepository userRepository;

    @Autowired
    public UserRegistrationValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return UserRegistrationForm.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        UserRegistrationForm form = (UserRegistrationForm) target;
        User foundUser = userRepository.findByName(form.getName());

        if (Objects.nonNull(foundUser)) {
            errors.rejectValue("name", "User with this name already exists");
        }

        if (!form.getPassword().equals(form.getConfirmPassword())) {
            errors.rejectValue("password", "Passwords do not match");
        }
    }

}
