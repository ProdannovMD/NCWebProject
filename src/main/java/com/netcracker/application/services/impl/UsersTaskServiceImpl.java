package com.netcracker.application.services.impl;

import com.netcracker.application.controllers.exceptions.ResourceNotFoundException;
import com.netcracker.application.model.ActiveTask;
import com.netcracker.application.model.Task;
import com.netcracker.application.model.User;
import com.netcracker.application.model.UsersTask;
import com.netcracker.application.repository.ActiveTaskRepository;
import com.netcracker.application.repository.TaskRepository;
import com.netcracker.application.repository.UsersTaskRepository;
import com.netcracker.application.services.UsersTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Component
public class UsersTaskServiceImpl implements UsersTaskService {
    private static final Long DEFAULT_TASK_ID = 1L;

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

    @Override
    public void addDefaultTaskForUser(User user) {
        UsersTask defaultUsersTask = new UsersTask();
        Task defaultTask = taskRepository.getById(DEFAULT_TASK_ID);
        if (Objects.isNull(defaultTask))
            throw new IllegalStateException("No default task is found");

        defaultUsersTask.setUser(user);
        defaultUsersTask.setTask(defaultTask);
        usersTaskRepository.save(defaultUsersTask);
        ActiveTask activeTask = new ActiveTask();
        activeTask.setTask(defaultUsersTask);
        activeTask.setStartTime(Instant.now());
        activeTaskRepository.save(activeTask);
    }

    @Override
    public UsersTask getDefaultTaskByUserId(Long id) {
        return getUsersTasksByUserId(id).stream()
                .filter(t -> t.getTask().getId().equals(DEFAULT_TASK_ID))
                .findFirst().orElseThrow(IllegalStateException::new);
    }

    @Override
    public void deleteUsersTaskById(Long id) {
        UsersTask usersTask = usersTaskRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
        if (usersTask.getTask().getId().equals(DEFAULT_TASK_ID))
            throw new AccessDeniedException("Illegal access to a task");
        if (usersTask.isActive()) {
            UsersTask defaultTask = getDefaultTaskByUserId(usersTask.getUser().getId());
            setUsersTaskActiveByUserId(usersTask.getUser().getId(), defaultTask);
        }

        usersTask.getChildrenTasks().forEach(ct -> deleteUsersTaskById(ct.getId()));

        activeTaskRepository.deleteAllByTaskId(usersTask.getId());
        usersTaskRepository.delete(usersTask);
        taskRepository.deleteById(usersTask.getId());
    }
}
