package com.netcracker.application.controllers.forms.converters;

import com.netcracker.application.controllers.forms.UserUpdateForm;
import com.netcracker.application.model.User;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UserUpdateFormConverter implements Converter<User, UserUpdateForm> {
    @Override
    public UserUpdateForm convert(User source) {
        UserUpdateForm form = new UserUpdateForm();
        form.setName(source.getUsername());
        return form;
    }
}
