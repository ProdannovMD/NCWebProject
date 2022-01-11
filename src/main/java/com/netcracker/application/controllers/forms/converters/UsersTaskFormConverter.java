package com.netcracker.application.controllers.forms.converters;

import com.netcracker.application.controllers.forms.UsersTaskForm;
import com.netcracker.application.model.User;
import com.netcracker.application.model.UsersTask;
import com.netcracker.application.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class UsersTaskFormConverter implements Converter<UsersTask, UsersTaskForm> {

    private final UserService userService;

    @Autowired
    public UsersTaskFormConverter(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UsersTaskForm convert(UsersTask source) {
        User currentUser = userService.getCurrentUser();
        UsersTaskForm form = new UsersTaskForm();
        if (!currentUser.getId().equals(source.getUser().getId()))
            throw new AccessDeniedException("Illegal access to a task");

        if (!source.getTask().getModifiable())
            throw new AccessDeniedException("Task is not modifiable");

        form.setId(source.getId());
        if (Objects.nonNull(source.getParentTask()))
            form.setParent(source.getParentTask().getId());
        form.setName(source.getTask().getName());
        form.setDescription(source.getTask().getDescription());

        return form;
    }
}
