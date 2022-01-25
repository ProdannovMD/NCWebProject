package com.netcracker.application.model;

import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Entity
@Table(name = "tasks")
public class Task {
    private static final String DATE_TIME_FORMAT = "yyyy.MM.dd HH:mm:ss";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "modifiable")
    private Boolean modifiable = true;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    @ManyToOne
    @JoinColumn(name = "status")
    @ColumnDefault("1")
    private Status status;

    @Column(name = "created_time")
    @ColumnDefault("NOW()")
    private Instant createdTime;

    @Column(name = "modified_time")
    @ColumnDefault("NOW()")
    private Instant modifiedTime;

    @Column(name = "description")
    private String description;

    @Column(name = "due_time")
    private Instant dueTime;

    @Column(name = "completed_time")
    private Instant completedTime;

    public Instant getCompletedTime() {
        return completedTime;
    }

    public String getCompletedTimeFormatted() {
        return DateTimeFormatter.ofPattern(DATE_TIME_FORMAT).withZone(ZoneId.systemDefault()).format(completedTime);
    }

    public void setCompletedTime(Instant completedTime) {
        this.completedTime = completedTime;
    }

    public Instant getDueTime() {
        return dueTime;
    }

    public String getDueTimeFormatted() {
        return DateTimeFormatter.ofPattern(DATE_TIME_FORMAT).withZone(ZoneId.systemDefault()).format(dueTime);
    }

    public void setDueTime(Instant dueTime) {
        this.dueTime = dueTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getModifiedTime() {
        return modifiedTime;
    }

    public String getModifiedTimeFormatted() {
        return DateTimeFormatter.ofPattern(DATE_TIME_FORMAT).withZone(ZoneId.systemDefault()).format(modifiedTime);
    }

    public void setModifiedTime(Instant modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    public Instant getCreatedTime() {
        return createdTime;
    }

    public String getCreatedTimeFormatted() {
        return DateTimeFormatter.ofPattern(DATE_TIME_FORMAT).withZone(ZoneId.systemDefault()).format(createdTime);
    }

    public void setCreatedTime(Instant createdTime) {
        this.createdTime = createdTime;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public Boolean getModifiable() {
        return modifiable;
    }

    public void setModifiable(Boolean modifiable) {
        this.modifiable = modifiable;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isOverdue() {
        if (Objects.nonNull(dueTime)) {
            Instant check = Objects.isNull(completedTime) ? Instant.now() : completedTime;
            return check.isAfter(dueTime);
        }
        return false;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}