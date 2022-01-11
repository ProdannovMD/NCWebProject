package com.netcracker.application.controllers.forms.converters;

import com.netcracker.application.controllers.forms.StatusForm;
import com.netcracker.application.model.Status;
import com.netcracker.application.model.Task;
import com.netcracker.application.model.UsersTask;
import com.netcracker.application.services.StatusService;
import com.netcracker.application.services.UsersTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UsersTaskStatusConverter implements Converter<StatusForm, UsersTask> {
    private final UsersTaskService usersTaskService;
    private final StatusService statusService;

    @Autowired
    public UsersTaskStatusConverter(UsersTaskService usersTaskService, StatusService statusService) {
        this.usersTaskService = usersTaskService;
        this.statusService = statusService;
    }

    @Override
    public UsersTask convert(StatusForm source) {
        UsersTask usersTask = usersTaskService.getUsersTaskById(source.getTask());
        Status status = statusService.getStatusById(source.getStatus());

        usersTask.getTask().setStatus(status);

        return usersTask;
    }
}
