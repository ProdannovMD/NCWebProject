package com.netcracker.application.services;

import com.netcracker.application.model.Task;

public interface TaskService {
    Task getTaskById(Long id);
}
