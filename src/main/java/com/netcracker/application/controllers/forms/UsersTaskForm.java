package com.netcracker.application.controllers.forms;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class UsersTaskForm {
    private Long id;
    @NotBlank(message = "{task.name.blank}")
    @Size(max = 50, message = "{tasks.name.long}")
    private String name;
    private Long parent;
    private String description;
    private String dueTime;

    public String getDueTime() {
        return dueTime;
    }

    public void setDueTime(String dueTime) {
        this.dueTime = dueTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getParent() {
        return parent;
    }

    public void setParent(Long parent) {
        this.parent = parent;
    }
}
