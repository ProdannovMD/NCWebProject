package com.netcracker.application.controllers.forms.converters;

import com.netcracker.application.controllers.forms.TaskCommentForm;
import com.netcracker.application.model.Task;
import com.netcracker.application.model.TaskComment;
import com.netcracker.application.model.User;
import com.netcracker.application.services.TaskService;
import com.netcracker.application.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class TaskCommentConverter implements Converter<TaskCommentForm, TaskComment> {
    private final TaskService taskService;
    private final UserService userService;

    @Autowired
    public TaskCommentConverter(TaskService taskService, UserService userService) {
        this.taskService = taskService;
        this.userService = userService;
    }

    @Override
    public TaskComment convert(TaskCommentForm source) {
        Task task = taskService.getTaskById(source.getTask());
        User user = userService.getUserById(source.getUser());

        TaskComment comment = new TaskComment();
        comment.setComment(source.getComment());
        comment.setTask(task);
        comment.setUser(user);
        comment.setCreationTime(Instant.now());

        return comment;
    }
}
