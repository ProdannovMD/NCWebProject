package com.netcracker.application.services;

import com.netcracker.application.model.Task;
import com.netcracker.application.model.TaskHistory;
import com.netcracker.application.model.User;

import java.util.List;

public interface TaskHistoryService {
    void saveTaskHistory(TaskHistory taskHistory);

    void saveTaskHistory(User user, Task task, String action);

    List<TaskHistory> getAllTaskHistory();
}
