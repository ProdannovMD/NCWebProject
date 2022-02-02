package com.netcracker.application.controllers.forms.converters;

import com.netcracker.application.controllers.forms.UsersTaskForm;
import com.netcracker.application.model.UsersTask;
import com.netcracker.logging.LogManager;
import com.netcracker.logging.loggers.Logger;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Component
public class UsersTaskFormConverter implements Converter<UsersTask, UsersTaskForm> {
    private final Logger logger = LogManager.getLogger("main.java", UsersTaskFormConverter.class);
    private final String DATE_FORMAT = "yyyy-MM-dd";

    @Override
    public UsersTaskForm convert(UsersTask source) {
        UsersTaskForm form = new UsersTaskForm();

        form.setId(source.getId());
        if (Objects.nonNull(source.getParentTask()))
            form.setParent(source.getParentTask().getId());
        form.setName(source.getTask().getName());
        form.setDescription(source.getTask().getDescription());
        if (Objects.nonNull(source.getTask().getDueTime())) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT)
                            .withZone(ZoneId.systemDefault());
            form.setDueTime(formatter.format(source.getTask().getDueTime()));
        }

        logger.debug("Users task " + source + " was converted into users task form " + form);

        return form;
    }
}
