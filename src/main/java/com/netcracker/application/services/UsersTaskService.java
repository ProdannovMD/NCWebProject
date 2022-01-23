package com.netcracker.application.services;

import com.netcracker.application.model.*;

import java.time.LocalDate;
import java.util.List;

public interface UsersTaskService {
    UsersTask getUsersTaskById(Long id);

    List<UsersTask> getUsersTasksByUserId(Long id);

    void setUsersTaskActiveByUserId(Long id, UsersTask usersTask);

    void saveUsersTask(UsersTask usersTask);

    void addDefaultTaskForUser(User user);

    UsersTask getDefaultTaskByUserId(Long id);

    void deleteUsersTaskById(Long id);

    List<Statistic> getStatisticsForTask(Task task, LocalDate start, LocalDate end);

    List<Statistic> getStatisticsForUsersTask(UsersTask usersTask, LocalDate start, LocalDate end);

    List<TaskComment> getTaskCommentsForUsersTask(UsersTask usersTask);

    List<TaskComment> getTaskCommentsForTask(Task usersTask);

    void saveTaskComment(TaskComment taskComment);

    List<UsersTask> getUsersTasksByTask(Task task);
}
