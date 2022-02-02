package com.netcracker.application.controllers.forms;

import javax.validation.constraints.NotNull;

public class StatusForm {
    @NotNull
    private Long task;
    @NotNull
    private Long status;

    public Long getTask() {
        return task;
    }

    public void setTask(Long task) {
        this.task = task;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "StatusForm{" +
                "task=" + task +
                ", status=" + status +
                '}';
    }
}
