package com.netcracker.application.controllers.forms.converters;

import com.netcracker.application.controllers.forms.UserUpdateForm;
import com.netcracker.application.model.User;
import com.netcracker.logging.LogManager;
import com.netcracker.logging.loggers.Logger;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UserUpdateFormConverter implements Converter<User, UserUpdateForm> {
    private final Logger logger = LogManager.getLogger("java.main", UserUpdateFormConverter.class);

    @Override
    public UserUpdateForm convert(User source) {
        UserUpdateForm form = new UserUpdateForm();
        form.setName(source.getUsername());

        logger.debug("User " + source + " was converted to user update form " + form);

        return form;
    }
}
