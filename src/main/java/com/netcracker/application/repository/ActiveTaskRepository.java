package com.netcracker.application.repository;

import com.netcracker.application.model.ActiveTask;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActiveTaskRepository extends JpaRepository<ActiveTask, Long> {
}