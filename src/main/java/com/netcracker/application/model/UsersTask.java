package com.netcracker.application.model;

import javafx.util.Pair;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Entity
@Table(name = "users_tasks")
public class UsersTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_id")
    private UsersTask parentTask;

    @OneToMany(mappedBy = "parentTask")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<UsersTask> childrenTasks;

    @OneToMany(mappedBy = "task")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<ActiveTask> usages;

    public List<ActiveTask> getUsages() {
        return usages;
    }

    public void setUsages(List<ActiveTask> usages) {
        this.usages = usages;
    }

    public UsersTask getParentTask() {
        return parentTask;
    }

    public void setParentTask(UsersTask parentTask) {
        this.parentTask = parentTask;
    }

    public List<UsersTask> getChildrenTasks() {
        return childrenTasks;
    }

    public void setChildrenTasks(List<UsersTask> childrenTasks) {
        this.childrenTasks = childrenTasks;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isActive() {
        return usages.stream().anyMatch(usage -> Objects.isNull(usage.getEndTime()));
    }

    private Pair<Long, Long> getActiveTimeRaw() {
        if (usages.size() == 0)
            return new Pair<>(0L, 0L);

        long hours = 0;
        long minutes = 0;
        TimeUnit timeHours = TimeUnit.HOURS;
        TimeUnit timeMinutes = TimeUnit.MINUTES;
        for (ActiveTask usage : usages) {
            Instant startTime = usage.getStartTime();
            Instant endTime = usage.getEndTime();
            if (Objects.isNull(endTime))
                endTime = Instant.now();
            LocalDateTime startDate = LocalDateTime.ofInstant(startTime, ZoneId.systemDefault());
            LocalDateTime endDate = LocalDateTime.ofInstant(endTime, ZoneId.systemDefault());
            Duration duration = Duration.between(startDate, endDate);
            minutes += timeMinutes.convert(duration.getSeconds(), TimeUnit.SECONDS);
        }
        hours = timeHours.convert(minutes, TimeUnit.MINUTES);
        minutes = Math.floorMod(minutes, 60);
        return new Pair<>(hours, minutes);
    }

    public String getActiveTime() {
        if (usages.size() == 0)
            return "0h 0m";

        Pair<Long, Long> activeTimeRaw = getActiveTimeRaw();
        long hours = activeTimeRaw.getKey();
        long minutes = activeTimeRaw.getValue();
        for (UsersTask childrenTask : childrenTasks) {
            Pair<Long, Long> childrenActiveTimeRaw = childrenTask.getActiveTimeRaw();
            hours += childrenActiveTimeRaw.getKey();
            minutes += childrenActiveTimeRaw.getValue();
        }
        TimeUnit timeHours = TimeUnit.HOURS;
        hours += timeHours.convert(minutes, TimeUnit.MINUTES);
        minutes = Math.floorMod(minutes, 60);

        return String.format("%dh %dm", hours, minutes);
    }

    @Override
    public String toString() {
        return "UsersTask{" +
                "id=" + id +
                ", user=" + user.getUsername() +
                ", task=" + task.getName() +
                ", parentTask=" + (Objects.nonNull(parentTask) ? parentTask.getTask().getName() : "None") +
                ", childrenTasks=" + childrenTasks.stream()
                .map(t -> t.getTask().getName()).collect(Collectors.toList()) +
                '}';
    }
}