package com.netcracker.application.repository;

import com.netcracker.application.model.UsersTask;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersTaskRepository extends JpaRepository<UsersTask, Long> {
}