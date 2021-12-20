package com.netcracker.application.services.impl;

import com.netcracker.application.model.Task;
import com.netcracker.application.model.UsersTask;
import com.netcracker.application.repository.TaskRepository;
import com.netcracker.application.repository.UsersTaskRepository;
import com.netcracker.application.services.UsersTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Objects;

@Component
public class UsersTaskServiceImpl implements UsersTaskService {

    private final UsersTaskRepository usersTaskRepository;
    private final TaskRepository taskRepository;

    @Autowired
    public UsersTaskServiceImpl(UsersTaskRepository usersTaskRepository, TaskRepository taskRepository) {
        this.usersTaskRepository = usersTaskRepository;
        this.taskRepository = taskRepository;
    }

    @Override
    public UsersTask getUsersTaskById(Long id) {
        return usersTaskRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    @Override
    public List<UsersTask> getUsersTasksByUserId(Long id) {
        return usersTaskRepository.findUsersTaskByUserId(id);
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
