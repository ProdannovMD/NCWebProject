package com.netcracker.application.repository;

import com.netcracker.application.model.Task;
import com.netcracker.application.model.UsersTask;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UsersTaskRepository extends JpaRepository<UsersTask, Long> {
    List<UsersTask> findUsersTaskByUserId(Long id);

    List<UsersTask> findAllByTask(Task task);
}