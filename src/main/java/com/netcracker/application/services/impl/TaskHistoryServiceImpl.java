package com.netcracker.application.services.impl;

import com.netcracker.application.model.Task;
import com.netcracker.application.model.TaskHistory;
import com.netcracker.application.model.User;
import com.netcracker.application.repository.TaskHistoryRepository;
import com.netcracker.application.services.TaskHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class TaskHistoryServiceImpl implements TaskHistoryService {

    private final TaskHistoryRepository taskHistoryRepository;

    @Autowired
    public TaskHistoryServiceImpl(TaskHistoryRepository taskHistoryRepository) {
        this.taskHistoryRepository = taskHistoryRepository;
    }

    @Override
    public void saveTaskHistory(TaskHistory taskHistory) {
        taskHistoryRepository.save(taskHistory);
    }

    @Override
    public void saveTaskHistory(User user, Task task, String action) {
        TaskHistory taskHistory = new TaskHistory();
        taskHistory.setUser(user);
        taskHistory.setTask(task);
        taskHistory.setAction(action);
        taskHistory.setDatetime(Instant.now());

        saveTaskHistory(taskHistory);
    }

    @Override
    public List<TaskHistory> getAllTaskHistory() {
        return taskHistoryRepository.findAll();
    }
}
