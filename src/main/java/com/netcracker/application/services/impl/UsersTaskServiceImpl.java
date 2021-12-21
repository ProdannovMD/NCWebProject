package com.netcracker.application.services.impl;

import com.netcracker.application.controllers.exceptions.ResourceNotFoundException;
import com.netcracker.application.model.ActiveTask;
import com.netcracker.application.model.Task;
import com.netcracker.application.model.UsersTask;
import com.netcracker.application.repository.ActiveTaskRepository;
import com.netcracker.application.repository.TaskRepository;
import com.netcracker.application.repository.UsersTaskRepository;
import com.netcracker.application.services.UsersTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Component
public class UsersTaskServiceImpl implements UsersTaskService {

    private final UsersTaskRepository usersTaskRepository;
    private final TaskRepository taskRepository;
    private final ActiveTaskRepository activeTaskRepository;

    @Autowired
    public UsersTaskServiceImpl(
            UsersTaskRepository usersTaskRepository,
            TaskRepository taskRepository,
            ActiveTaskRepository activeTaskRepository
    ) {
        this.usersTaskRepository = usersTaskRepository;
        this.taskRepository = taskRepository;
        this.activeTaskRepository = activeTaskRepository;
    }

    @Override
    public UsersTask getUsersTaskById(Long id) {
        return usersTaskRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
    }

    @Override
    public List<UsersTask> getUsersTasksByUserId(Long id) {
        return usersTaskRepository.findUsersTaskByUserId(id);
    }

    @Override
    public void setUsersTaskActiveByUserId(Long id, UsersTask usersTask) {
        if (usersTask.isActive())
            return;

        List<UsersTask> tasks = getUsersTasksByUserId(id);
        Instant now = Instant.now();
        for (UsersTask task : tasks) {
            if (task.isActive()) {
                ActiveTask activeTask = task.getUsages().stream()
                        .filter(usage -> Objects.isNull(usage.getEndTime()))
                        .findFirst().orElseThrow(IllegalStateException::new);
                activeTask.setEndTime(now);
                activeTaskRepository.save(activeTask);
            }
        }
        ActiveTask newActiveTask = new ActiveTask();
        newActiveTask.setTask(usersTask);
        newActiveTask.setStartTime(now);
        activeTaskRepository.save(newActiveTask);
    }

    @Override
    public void saveUsersTask(UsersTask usersTask) {
        Task savedTask = usersTask.getTask();
        if (Objects.isNull(savedTask.getId())) {
            savedTask = taskRepository.save(usersTask.getTask());
        }
        usersTask.setTask(savedTask);
        usersTaskRepository.save(usersTask);
    }
}
