package com.netcracker.application.model;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "active_tasks")
public class ActiveTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "task_id", nullable = false)
    private UsersTask task;

    @Column(name = "start_time", nullable = false)
    private Instant startTime;

    @Column(name = "end_time")
    private Instant endTime;

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public UsersTask getTask() {
        return task;
    }

    public void setTask(UsersTask task) {
        this.task = task;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "ActiveTask{" +
                "id=" + id +
                ", task=" + task +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}