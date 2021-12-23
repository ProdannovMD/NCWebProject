package com.netcracker.application.repository;

import com.netcracker.application.model.ActiveTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface ActiveTaskRepository extends JpaRepository<ActiveTask, Long> {

    @Transactional
    void deleteAllByTaskId(Long id);
}