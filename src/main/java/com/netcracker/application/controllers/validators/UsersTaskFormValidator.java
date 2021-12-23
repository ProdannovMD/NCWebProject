package com.netcracker.application.controllers.validators;

import com.netcracker.application.controllers.forms.UsersTaskForm;
import com.netcracker.application.model.User;
import com.netcracker.application.model.UsersTask;
import com.netcracker.application.services.UserService;
import com.netcracker.application.services.UsersTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Locale;
import java.util.Objects;

@Component
public class UsersTaskFormValidator implements Validator, MessageSourceAware {

    private MessageSource messageSource;

    private final UserService userService;
    private final UsersTaskService usersTaskService;

    @Autowired
    public UsersTaskFormValidator(UserService userService, UsersTaskService usersTaskService) {
        this.userService = userService;
        this.usersTaskService = usersTaskService;
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return UsersTaskForm.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        UsersTaskForm form = (UsersTaskForm) target;
        User currentUser = userService.getCurrentUser();
        if (Objects.nonNull(form.getId())) {
            UsersTask task = usersTaskService.getUsersTaskById(form.getId());
            if (!task.getUser().getId().equals(currentUser.getId())) {
                throw new AccessDeniedException("Illegal access to a task");
            }

            if (Objects.nonNull(form.getParent()) && form.getParent() > 0 && task.getId().equals(form.getParent()))
                errors.rejectValue(
                        "parent",
                        "tasks.parent.self",
                        messageSource.getMessage("tasks.parent.self", null, Locale.getDefault())
                );

        }

        if (Objects.nonNull(form.getParent()) && form.getParent() > 0) {
            UsersTask parentTask = usersTaskService.getUsersTaskById(form.getParent());
            if (!parentTask.getUser().getId().equals(currentUser.getId())) {
                throw new AccessDeniedException("Illegal access to a task");
            }
            if (Objects.nonNull(parentTask.getParentTask())) {
                errors.rejectValue(
                        "parent",
                        "tasks.parent.subtask",
                        messageSource.getMessage("tasks.parent.subtask", null, Locale.getDefault())
                );
            }
        }


    }
}
