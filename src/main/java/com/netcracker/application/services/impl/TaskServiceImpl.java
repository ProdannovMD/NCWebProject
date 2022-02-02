package com.netcracker.application.services.impl;

import com.netcracker.application.controllers.exceptions.ResourceNotFoundException;
import com.netcracker.application.model.Task;
import com.netcracker.application.repository.TaskRepository;
import com.netcracker.application.repository.UsersTaskRepository;
import com.netcracker.application.services.TaskService;
import com.netcracker.application.services.UsersTaskService;
import com.netcracker.logging.LogManager;
import com.netcracker.logging.loggers.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class TaskServiceImpl implements TaskService {
    private final Logger logger = LogManager.getLogger("main.java", TaskService.class);
    private final TaskRepository taskRepository;
    private final UsersTaskRepository usersTaskRepository;

    @Autowired
    public TaskServiceImpl(TaskRepository taskRepository, UsersTaskRepository usersTaskRepository) {
        this.taskRepository = taskRepository;
        this.usersTaskRepository = usersTaskRepository;
    }

    @Override
    public Task getTaskById(Long id) {
        Task task = taskRepository.getById(id);
        if (Objects.isNull(task))
            throw new ResourceNotFoundException("Task not found");
        return task;
    }

    @Override
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    @Override
    public boolean isUsed(Task task) {
        return usersTaskRepository.findAllByTask(task).size() > 0;
    }

    @Override
    public void deleteTask(Task task) {
        if (!isUsed(task)) {
            taskRepository.delete(task);
            logger.info("Task " + task + " has been deleted");
        }
    }
}
