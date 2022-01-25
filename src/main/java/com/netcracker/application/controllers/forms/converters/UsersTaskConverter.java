package com.netcracker.application.controllers.forms.converters;

import com.netcracker.application.controllers.forms.UsersTaskForm;
import com.netcracker.application.model.Task;
import com.netcracker.application.model.User;
import com.netcracker.application.model.UsersTask;
import com.netcracker.application.services.StatusService;
import com.netcracker.application.services.UserService;
import com.netcracker.application.services.UsersTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjuster;
import java.util.List;
import java.util.Objects;

@Component
public class UsersTaskConverter implements Converter<UsersTaskForm, UsersTask> {
    private final UserService userService;
    private final UsersTaskService usersTaskService;
    private final StatusService statusService;

    @Autowired
    public UsersTaskConverter(UserService userService, UsersTaskService usersTaskService, StatusService statusService) {
        this.userService = userService;
        this.usersTaskService = usersTaskService;
        this.statusService = statusService;
    }

    @Override
    public UsersTask convert(UsersTaskForm source) {
        User currentUser = userService.getCurrentUser();
        Task newTask;
        UsersTask usersTask;
        boolean isNew = false;
        if (Objects.isNull(source.getId())) {
            usersTask = new UsersTask();
            newTask = new Task();
            isNew = true;
        }
        else {
            usersTask = usersTaskService.getUsersTaskById(source.getId());
            newTask = usersTask.getTask();
        }
        newTask.setName(source.getName());
        if (isNew) {
            newTask.setCreatedBy(currentUser);
            newTask.setCreatedTime(Instant.now());
            newTask.setStatus(statusService.getDefaultStatus());
        }
        newTask.setModifiedTime(Instant.now());
        newTask.setDescription(source.getDescription());
        if (Objects.nonNull(source.getDueTime()) && !source.getDueTime().isEmpty()) {
            try {
                LocalDate date = LocalDate.parse(source.getDueTime());

                newTask.setDueTime(date.atStartOfDay(ZoneId.systemDefault()).plusDays(1).minusSeconds(1).toInstant());
            } catch (DateTimeParseException | IllegalArgumentException ignore) {}
        }

        usersTask.setId(source.getId());
        usersTask.setTask(newTask);
        if (isNew)
            usersTask.setUser(currentUser);
        if (source.getParent() > 0) {
            UsersTask parentTask = usersTaskService.getUsersTaskById(source.getParent());
            usersTask.setParentTask(parentTask);
        }
        return usersTask;
    }
}
