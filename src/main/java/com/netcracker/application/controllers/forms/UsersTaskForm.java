package com.netcracker.application.controllers.forms;

import javax.validation.constraints.Size;

public class UsersTaskForm {
    private Long id;
    @Size(max = 50, message = "{tasks.name.long}")
    private String name;
    private Long parent;

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
