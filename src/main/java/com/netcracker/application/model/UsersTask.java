package com.netcracker.application.model;

import javafx.util.Pair;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.time.*;
import java.time.chrono.ChronoLocalDate;
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

    public Long getActiveTimeForDateRaw(LocalDate start, LocalDate end) {
        if (usages.size() == 0)
            return 0L;

        long seconds = 0L;
        for (ActiveTask usage : usages) {
            Instant startTime = usage.getStartTime();
            Instant endTime = usage.getEndTime();
            if (Objects.isNull(endTime))
                endTime = Instant.now();

            if (
                    start.atStartOfDay(ZoneId.systemDefault()).toInstant().isAfter(endTime)
                    || end.atStartOfDay(ZoneId.systemDefault()).toInstant().isBefore(startTime)
            ) {
                continue;
            }

            LocalDateTime startDateTime = start.atStartOfDay(ZoneId.systemDefault()).toInstant().isAfter(startTime) ?
                    start.atStartOfDay() :
                    LocalDateTime.ofInstant(startTime, ZoneId.systemDefault());
            LocalDateTime endDateTime = end.atStartOfDay(ZoneId.systemDefault()).toInstant().isBefore(endTime) ?
                    end.atStartOfDay() :
                    LocalDateTime.ofInstant(endTime, ZoneId.systemDefault());

            seconds += Duration.between(startDateTime, endDateTime).getSeconds();
        }
        return seconds;
    }

    public Long getActiveTimeRaw() {
//        if (usages.size() == 0)
//            return 0L;
//
//        long seconds = 0;
//        for (ActiveTask usage : usages) {
//            Instant startTime = usage.getStartTime();
//            Instant endTime = usage.getEndTime();
//            if (Objects.isNull(endTime))
//                endTime = Instant.now();
//            LocalDateTime startDate = LocalDateTime.ofInstant(startTime, ZoneId.systemDefault());
//            LocalDateTime endDate = LocalDateTime.ofInstant(endTime, ZoneId.systemDefault());
//            Duration duration = Duration.between(startDate, endDate);
//            seconds += duration.getSeconds();
//        }
//        return seconds;
        return getActiveTimeForDateRaw(LocalDate.MIN, LocalDate.MAX);
    }

    public String getActiveTime() {
        if (usages.size() == 0)
            return "0h 0m";

        long seconds = getActiveTimeRaw();
        for (UsersTask childrenTask : childrenTasks) {
            seconds += childrenTask.getActiveTimeRaw();
        }
        TimeUnit timeHours = TimeUnit.HOURS;
        TimeUnit timeMinutes = TimeUnit.MINUTES;
        long hours = timeHours.convert(seconds, TimeUnit.SECONDS);
        long minutes = Math.floorMod(timeMinutes.convert(seconds, TimeUnit.SECONDS), 60);

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