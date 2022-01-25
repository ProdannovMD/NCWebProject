package com.netcracker.application.services;

import com.netcracker.application.model.Task;

import java.util.List;

public interface TaskService {
    Task getTaskById(Long id);

    List<Task> getAllTasks();

    boolean isUsed(Task task);

    void deleteTask(Task task);
}
