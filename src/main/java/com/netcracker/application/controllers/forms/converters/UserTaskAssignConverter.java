package com.netcracker.application.controllers.forms.converters;

import com.netcracker.application.controllers.forms.AssignTaskForm;
import com.netcracker.application.model.Task;
import com.netcracker.application.model.User;
import com.netcracker.application.model.UsersTask;
import com.netcracker.application.services.TaskService;
import com.netcracker.application.services.UserService;
import com.netcracker.application.services.UsersTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UserTaskAssignConverter implements Converter<AssignTaskForm, UsersTask> {
    private final UserService userService;
    private final TaskService taskService;
    private final UsersTaskService usersTaskService;

    @Autowired
    public UserTaskAssignConverter(UserService userService, TaskService taskService, UsersTaskService usersTaskService) {
        this.userService = userService;
        this.taskService = taskService;
        this.usersTaskService = usersTaskService;
    }

    @Override
    public UsersTask convert(AssignTaskForm source) {
        User user = userService.getUserById(source.getUser());
        Task task = taskService.getTaskById(source.getTask());
        UsersTask usersTask = new UsersTask();
        usersTask.setUser(user);
        usersTask.setTask(task);
        if (source.getParent() > 0) {
            UsersTask parentTask = usersTaskService.getUsersTaskById(source.getParent());
            usersTask.setParentTask(parentTask);
        }

        return usersTask;
    }
}
