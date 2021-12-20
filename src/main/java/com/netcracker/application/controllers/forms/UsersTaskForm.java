package com.netcracker.application.controllers.forms;

import org.hibernate.validator.constraints.Length;

public class UsersTaskForm {
    @Length(max = 50)
    private String name;
    private Long parent;

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
