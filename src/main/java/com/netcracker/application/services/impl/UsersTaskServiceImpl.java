package com.netcracker.application.services.impl;

import com.netcracker.application.controllers.exceptions.ResourceNotFoundException;
import com.netcracker.application.model.*;
import com.netcracker.application.repository.ActiveTaskRepository;
import com.netcracker.application.repository.TaskCommentRepository;
import com.netcracker.application.repository.TaskRepository;
import com.netcracker.application.repository.UsersTaskRepository;
import com.netcracker.application.services.TaskHistoryService;
import com.netcracker.application.services.UsersTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Component
public class UsersTaskServiceImpl implements UsersTaskService {
    private static final Long DEFAULT_TASK_ID = 1L;

    private final UsersTaskRepository usersTaskRepository;
    private final TaskRepository taskRepository;
    private final ActiveTaskRepository activeTaskRepository;
    private final TaskCommentRepository taskCommentRepository;
    private final TaskHistoryService taskHistoryService;

    @Autowired
    public UsersTaskServiceImpl(
            UsersTaskRepository usersTaskRepository,
            TaskRepository taskRepository,
            ActiveTaskRepository activeTaskRepository,
            TaskCommentRepository taskCommentRepository,
            TaskHistoryService taskHistoryService
    ) {
        this.usersTaskRepository = usersTaskRepository;
        this.taskRepository = taskRepository;
        this.activeTaskRepository = activeTaskRepository;
        this.taskCommentRepository = taskCommentRepository;
        this.taskHistoryService = taskHistoryService;
    }

    @Override
    public UsersTask getUsersTaskById(Long id) {
        return usersTaskRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
    }

    @Override
    public List<UsersTask> getUsersTasksByUserId(Long id) {
        return usersTaskRepository.findUsersTaskByUserId(id);
    }

    @Override
    public void setUsersTaskActiveByUserId(Long id, UsersTask usersTask) {
        if (usersTask.isActive())
            return;

        List<UsersTask> tasks = getUsersTasksByUserId(id);
        Instant now = Instant.now();
        for (UsersTask task : tasks) {
            if (task.isActive()) {
                ActiveTask activeTask = task.getUsages().stream()
                        .filter(usage -> Objects.isNull(usage.getEndTime()))
                        .findFirst().orElseThrow(IllegalStateException::new);
                activeTask.setEndTime(now);
                activeTaskRepository.save(activeTask);
            }
        }
        ActiveTask newActiveTask = new ActiveTask();
        newActiveTask.setTask(usersTask);
        newActiveTask.setStartTime(now);
        activeTaskRepository.save(newActiveTask);

        taskHistoryService.saveTaskHistory(usersTask.getUser(), usersTask.getTask(), "Task set active");
    }

    @Override
    public void saveUsersTask(UsersTask usersTask) {
        Task savedTask = usersTask.getTask();
        boolean newTask = Objects.isNull(savedTask.getId());
        if (newTask) {
            savedTask = taskRepository.save(usersTask.getTask());
        }
        usersTask.setTask(savedTask);
        usersTaskRepository.save(usersTask);

        taskHistoryService.saveTaskHistory(
                usersTask.getUser(), usersTask.getTask(),
                newTask ? "Task created" : "Task updated"
        );
    }

    @Override
    public void addDefaultTaskForUser(User user) {
        UsersTask defaultUsersTask = new UsersTask();
        Task defaultTask = taskRepository.getById(DEFAULT_TASK_ID);
        if (Objects.isNull(defaultTask))
            throw new IllegalStateException("No default task is found");

        defaultUsersTask.setUser(user);
        defaultUsersTask.setTask(defaultTask);
        usersTaskRepository.save(defaultUsersTask);
        ActiveTask activeTask = new ActiveTask();
        activeTask.setTask(defaultUsersTask);
        activeTask.setStartTime(Instant.now());
        activeTaskRepository.save(activeTask);
    }

    @Override
    public UsersTask getDefaultTaskByUserId(Long id) {
        return getUsersTasksByUserId(id).stream()
                .filter(t -> t.getTask().getId().equals(DEFAULT_TASK_ID))
                .findFirst().orElseThrow(IllegalStateException::new);
    }

    @Override
    public void deleteUsersTaskById(Long id) {
        UsersTask usersTask = usersTaskRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
        if (usersTask.getTask().getId().equals(DEFAULT_TASK_ID))
            throw new AccessDeniedException("Illegal access to a task");
        if (usersTask.isActive()) {
            UsersTask defaultTask = getDefaultTaskByUserId(usersTask.getUser().getId());
            setUsersTaskActiveByUserId(usersTask.getUser().getId(), defaultTask);
        }

        usersTask.getChildrenTasks().forEach(ct -> deleteUsersTaskById(ct.getId()));

        usersTaskRepository.delete(usersTask);

        taskHistoryService.saveTaskHistory(usersTask.getUser(), usersTask.getTask(), "Task deleted from users list");
    }

    @Override
    public List<Statistic> getStatisticsForUsersTask(UsersTask usersTask, LocalDate start, LocalDate end) {
        if (end.isBefore(start))
            throw new IllegalStateException("Start date should be before the end date");

        List<UsersTask> tasks = new ArrayList<>();
        List<Long> statisticsTimeRaw = new ArrayList<>();
        Long totalTimeRaw = 0L;
        List<Statistic> statistics = new ArrayList<>();
        tasks.add(usersTask);
        tasks.addAll(usersTask.getChildrenTasks());
        if (tasks.stream().allMatch(task -> task.getUsages().size() == 0))
            return statistics;

        LocalDate minDate = tasks.stream()
                .flatMap(task -> task.getUsages().stream())
                .map(usage -> LocalDateTime.ofInstant(usage.getStartTime(), ZoneId.systemDefault()).toLocalDate())
                .min((d1, d2) -> {
                    if (d1.isBefore(d2))
                        return -1;
                    if (d2.isBefore(d1))
                        return 1;
                    return 0;
                }).orElse(start);
        LocalDate maxDate = tasks.stream()
                .flatMap(task -> task.getUsages().stream())
                .map(usage -> LocalDateTime.ofInstant(
                                        Objects.isNull(usage.getEndTime()) ? Instant.now() : usage.getEndTime(),
                                        ZoneId.systemDefault()
                                )
                                .toLocalDate()
                )
                .max((d1, d2) -> {
                    if (d1.isBefore(d2))
                        return -1;
                    if (d2.isBefore(d1))
                        return 1;
                    return 0;
                }).orElse(end);

        start = minDate.isAfter(start) ? minDate : start;
        end = maxDate.isBefore(end) ? maxDate : end;

        LocalDate current = LocalDate.from(start);
        while (current.isBefore(end) || current.isEqual(end)) {
            for (UsersTask task : tasks) {
                Long statisticTime = task.getActiveTimeForDateRaw(current, current.plusDays(1));
                totalTimeRaw += statisticTime;
                long hours = TimeUnit.HOURS.convert(statisticTime, TimeUnit.SECONDS);
                long minutes = Math.floorMod(TimeUnit.MINUTES.convert(statisticTime, TimeUnit.SECONDS), 60);

                String statisticTimeString = String.format("%sh %sm", hours, minutes);
                Statistic statistic = Statistic.builder()
                        .taskName(task.getTask().getName())
                        .date(current.format(DateTimeFormatter.ISO_LOCAL_DATE))
                        .activeTime(statisticTimeString)
                        .build();

                statistics.add(
                        statistic
                );
                statisticsTimeRaw.add(statisticTime);

            }

            current = current.plusDays(1);
        }

        for (int i = 0; i < statistics.size(); i++) {
            double activePercent = statisticsTimeRaw.get(i) * 1. / totalTimeRaw;
            statistics.get(i).setActivePercent(activePercent * 100);
        }

        return statistics;
    }

    @Override
    public List<TaskComment> getTaskCommentsForUsersTask(UsersTask usersTask) {

        return taskCommentRepository.getByTask(usersTask.getTask());
    }

    @Override
    public List<TaskComment> getTaskCommentsForTask(Task task) {
        return taskCommentRepository.getByTask(task);
    }

    @Override
    public void saveTaskComment(TaskComment taskComment) {
        taskCommentRepository.save(taskComment);

        taskHistoryService.saveTaskHistory(taskComment.getUser(), taskComment.getTask(), "Task comment added");
    }
}
