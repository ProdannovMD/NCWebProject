package com.netcracker.application.controllers.forms.converters;

import com.netcracker.application.controllers.forms.UsersTaskForm;
import com.netcracker.application.model.User;
import com.netcracker.application.model.UsersTask;
import com.netcracker.application.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Component
public class UsersTaskFormConverter implements Converter<UsersTask, UsersTaskForm> {
    private final String DATE_FORMAT = "yyyy-MM-dd";
    private final UserService userService;

    @Autowired
    public UsersTaskFormConverter(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UsersTaskForm convert(UsersTask source) {
        User currentUser = userService.getCurrentUser();
        UsersTaskForm form = new UsersTaskForm();

        form.setId(source.getId());
        if (Objects.nonNull(source.getParentTask()))
            form.setParent(source.getParentTask().getId());
        form.setName(source.getTask().getName());
        form.setDescription(source.getTask().getDescription());
        if (Objects.nonNull(source.getTask().getDueTime())) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT)
                            .withZone(ZoneId.systemDefault());
            form.setDueTime(formatter.format(source.getTask().getDueTime()));
        }

        return form;
    }
}
