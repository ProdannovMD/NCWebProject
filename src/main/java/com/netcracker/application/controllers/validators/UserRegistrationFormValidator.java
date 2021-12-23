package com.netcracker.application.controllers.validators;

import com.netcracker.application.controllers.forms.UserRegistrationForm;
import com.netcracker.application.model.User;
import com.netcracker.application.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Locale;
import java.util.Objects;

@Component
public class UserRegistrationFormValidator implements Validator, MessageSourceAware {

    private MessageSource messageSource;

    private final UserRepository userRepository;

    @Autowired
    public UserRegistrationFormValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
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
            errors.rejectValue(
                    "name",
                    "registration.username.non-unique",
                    messageSource.getMessage("registration.username.non-unique", null, Locale.getDefault())
            );
        }

        if (!form.getPassword().equals(form.getConfirmPassword())) {
            errors.rejectValue(
                    "password",
                    "registration.password.no-match",
                    messageSource.getMessage("registration.password.no-match", null, Locale.getDefault())
            );
        }
    }

}
