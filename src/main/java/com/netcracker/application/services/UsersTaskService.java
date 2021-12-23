package com.netcracker.application.services;

import com.netcracker.application.model.User;
import com.netcracker.application.model.UsersTask;

import java.util.List;

public interface UsersTaskService {
    UsersTask getUsersTaskById(Long id);

    List<UsersTask> getUsersTasksByUserId(Long id);

    void setUsersTaskActiveByUserId(Long id, UsersTask usersTask);

    void saveUsersTask(UsersTask usersTask);

    void addDefaultTaskForUser(User user);

    UsersTask getDefaultTaskByUserId(Long id);

    void deleteUsersTaskById(Long id);
}
