package com.netcracker.application.controllers.forms.converters;

import com.netcracker.application.controllers.forms.UsersTaskForm;
import com.netcracker.application.model.Task;
import com.netcracker.application.model.User;
import com.netcracker.application.model.UsersTask;
import com.netcracker.application.services.UserService;
import com.netcracker.application.services.UsersTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UsersTaskConverter implements Converter<UsersTaskForm, UsersTask> {

    private final UserService userService;
    private final UsersTaskService usersTaskService;

    @Autowired
    public UsersTaskConverter(UserService userService, UsersTaskService usersTaskService) {
        this.userService = userService;
        this.usersTaskService = usersTaskService;
    }

    @Override
    public UsersTask convert(UsersTaskForm source) {
        User currentUser = userService.getCurrentUser();
        Task newTask = new Task();
        newTask.setName(source.getName());

        UsersTask usersTask = new UsersTask();
        usersTask.setId(source.getId());
        usersTask.setTask(newTask);
        usersTask.setUser(currentUser);
        if (source.getParent() > 0) {
            UsersTask parentTask = usersTaskService.getUsersTaskById(source.getParent());
            usersTask.setParentTask(parentTask);
        }
        return usersTask;
    }
}
