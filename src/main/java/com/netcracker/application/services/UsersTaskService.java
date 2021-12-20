package com.netcracker.application.services;

import com.netcracker.application.model.UsersTask;

import java.util.List;

public interface UsersTaskService {
    UsersTask getUsersTaskById(Long id);

    List<UsersTask> getUsersTasksByUserId(Long id);

    void saveUsersTask(UsersTask usersTask);
}
