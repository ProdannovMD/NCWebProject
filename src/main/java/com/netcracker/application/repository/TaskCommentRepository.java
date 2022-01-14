package com.netcracker.application.repository;

import com.netcracker.application.model.Task;
import com.netcracker.application.model.TaskComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskCommentRepository extends JpaRepository<TaskComment, Long> {
    List<TaskComment> getByTask(Task task);
}