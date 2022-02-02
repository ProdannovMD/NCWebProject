package com.netcracker.application.controllers.forms;

public class AssignTaskForm {
    private Long user;
    private Long task;
    private Long parent;

    public Long getParent() {
        return parent;
    }

    public void setParent(Long parent) {
        this.parent = parent;
    }

    public Long getUser() {
        return user;
    }

    public void setUser(Long user) {
        this.user = user;
    }

    public Long getTask() {
        return task;
    }

    public void setTask(Long task) {
        this.task = task;
    }

    @Override
    public String toString() {
        return "AssignTaskForm{" +
                "user=" + user +
                ", task=" + task +
                ", parent=" + parent +
                '}';
    }
}
