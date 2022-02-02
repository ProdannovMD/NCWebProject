package com.netcracker.application.controllers;

import com.netcracker.application.controllers.forms.StatusForm;
import com.netcracker.application.controllers.forms.TaskCommentForm;
import com.netcracker.application.controllers.forms.UserRegistrationForm;
import com.netcracker.application.controllers.forms.UsersTaskForm;
import com.netcracker.application.controllers.validators.UsersTaskFormValidator;
import com.netcracker.application.model.TaskComment;
import com.netcracker.application.model.User;
import com.netcracker.application.model.UsersTask;
import com.netcracker.application.services.StatusService;
import com.netcracker.application.services.UserService;
import com.netcracker.application.services.UsersTaskService;
import com.netcracker.logging.LogManager;
import com.netcracker.logging.loggers.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.convert.ConversionService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/tasks")
public class UsersTaskController {
    private final Logger logger = LogManager.getLogger("main.java", UsersTaskController.class);
    private final UserService userService;
    private final UsersTaskService usersTaskService;
    private final StatusService statusService;
    private UsersTaskFormValidator usersTaskFormValidator;
    private ConversionService conversionService;

    @Autowired
    public UsersTaskController(UserService userService, UsersTaskService usersTaskService, StatusService statusService) {
        this.userService = userService;
        this.usersTaskService = usersTaskService;
        this.statusService = statusService;
    }

    @Autowired
    public void setConversionService(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @Autowired
    public void setUsersTaskFormValidator(UsersTaskFormValidator usersTaskFormValidator) {
        this.usersTaskFormValidator = usersTaskFormValidator;
    }

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        Optional<Object> userTaskTarget = Optional.ofNullable(binder.getTarget())
                .filter(field -> field.getClass().equals(UserRegistrationForm.class));
        if (userTaskTarget.isPresent())
            binder.setValidator(usersTaskFormValidator);
    }

    @GetMapping("/{id}")
    public String getUsersTask(@PathVariable Long id, Model model) {
        User currentUser = userService.getCurrentUser();
        UsersTask usersTask = usersTaskService.getUsersTaskById(id);
        List<TaskComment> comments = usersTaskService.getTaskCommentsForUsersTask(usersTask);
        if (!currentUser.getId().equals(usersTask.getUser().getId())) {
            if (currentUser.hasRole("ROLE_ADMIN"))
                return String.format("redirect:/admin/users/%d/tasks/%d", usersTask.getUser().getId(), id);

            throw new AccessDeniedException("Invalid access to a task");
        }

        StatusForm form = new StatusForm();
        form.setTask(usersTask.getId());
        form.setStatus(usersTask.getTask().getStatus().getId());

        model.addAttribute("comments", comments);
        model.addAttribute("user", currentUser);
        model.addAttribute("task", usersTask);
        model.addAttribute("statuses", statusService.getAllStatuses());
        model.addAttribute("form", form);

        logger.info("User " + currentUser + " accessed '/tasks/" + id + "' page");

        return "task/usersTask";
    }

    @GetMapping("/save")
    public String saveUsersTaskForm(Model model, Long parent, Long id) {
        User currentUser = userService.getCurrentUser();
        List<UsersTask> tasks = usersTaskService.getUsersTasksByUserId(currentUser.getId());
        System.out.println(tasks);


        UsersTaskForm form = new UsersTaskForm();
        if (Objects.isNull(id)) {
            if (Objects.nonNull(parent))
                form.setParent(parent);
        } else {
            UsersTask task = usersTaskService.getUsersTaskById(id);

            if (
                    !currentUser.getId().equals(task.getUser().getId()) &&
                            !currentUser.getId().equals(task.getTask().getCreatedBy().getId())
            )
                throw new AccessDeniedException("Illegal access to a task");

            if (!task.getTask().getModifiable())
                throw new AccessDeniedException("Task is not modifiable");

            form = conversionService.convert(task, UsersTaskForm.class);
        }

        model.addAttribute("tasks", tasks);
        model.addAttribute("form", form);

        logger.info("User " + currentUser + " accessed '/tasks/save' page");

        return "task/saveUsersTask";
    }

    @PostMapping("/save")
    public String saveUsersTask(
            @Valid @ModelAttribute("form") UsersTaskForm form,
            BindingResult bindingResult, Model model) {
        User currentUser = userService.getCurrentUser();
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList());
            model.addAttribute("errors", errorMessages);

            logger.info("Task save by user " + currentUser + " failed with errors: " + errorMessages);

            return "task/saveUsersTask";
        }

        UsersTask task = conversionService.convert(form, UsersTask.class);
        if (
                !currentUser.getId().equals(task.getUser().getId()) &&
                        !currentUser.getId().equals(task.getTask().getCreatedBy().getId())
        )
            throw new AccessDeniedException("Illegal access to a task");

        if (!task.getTask().getModifiable())
            throw new AccessDeniedException("Task is not modifiable");

        usersTaskService.saveUsersTask(task);

        logger.info("Task save by user " + currentUser + " successful");

        return "redirect:/profile";
    }

    @PostMapping("/delete")
    public String deleteUsersTask(@RequestParam("task") Long id) {
        User currentUser = userService.getCurrentUser();
        UsersTask task = usersTaskService.getUsersTaskById(id);
        if (
                !task.getUser().getId().equals(currentUser.getId()) &&
                        !task.getTask().getCreatedBy().getId().equals(currentUser.getId())
        )
            throw new AccessDeniedException("Invalid access to a task");

        usersTaskService.deleteUsersTaskById(id);

        logger.info("Task deletion by user " + currentUser + " successful");

        return "redirect:/profile";
    }

    @GetMapping("/active")
    public String getActiveTask() {
        User currentUser = userService.getCurrentUser();
        List<UsersTask> userTasks = usersTaskService.getUsersTasksByUserId(currentUser.getId());
        UsersTask activeTask = userTasks.stream()
                .filter(UsersTask::isActive)
                .findFirst().orElseThrow(IllegalStateException::new);

        logger.info("User " + currentUser + " accessed '/tasks/active' page");

        return String.format("redirect:/tasks/%d", activeTask.getId());
    }

    @GetMapping
    public String tasks() {
        return "redirect:/profile";
    }

    @PostMapping("/active")
    public String setActiveTask(@RequestParam("task") Long taskId) {
        User currentUser = userService.getCurrentUser();
        UsersTask task = usersTaskService.getUsersTaskById(taskId);
        usersTaskService.setUsersTaskActiveByUserId(currentUser.getId(), task);

        logger.info("Active task changed by user " + currentUser + " successfully");

        return "redirect:/profile";
    }

    @PostMapping("/change_status")
    public String changeStatus(@Valid @ModelAttribute("form") StatusForm form, BindingResult bindingResult) {
        User currentUser = userService.getCurrentUser();
        if (bindingResult.hasErrors()) {
            logger.info("Task status change by user " + currentUser + " failed");
            return "redirect:/profile";
        }

        UsersTask usersTask = conversionService.convert(form, UsersTask.class);
        usersTaskService.saveUsersTask(usersTask);

        logger.info("Task status changed by user " + currentUser + " successfully");

        return String.format("redirect:/tasks/%d", usersTask.getId());
    }

    @GetMapping("/{id}/comments/save")
    public String saveCommentForm(@PathVariable Long id, Model model) {
        User currentUser = userService.getCurrentUser();
        UsersTask usersTask = usersTaskService.getUsersTaskById(id);

        if (
                !usersTask.getTask().getModifiable() || (
                        !currentUser.getId().equals(usersTask.getUser().getId()) &&
                                !currentUser.getId().equals(usersTask.getTask().getCreatedBy().getId())
                )
        ) {
            throw new AccessDeniedException("Illegal access to a task");
        }

        TaskCommentForm form = new TaskCommentForm();
        form.setUser(currentUser.getId());
        form.setTask(usersTask.getTask().getId());

        model.addAttribute("task", usersTask);
        model.addAttribute("form", form);

        logger.info("User " + currentUser + " accessed '/tasks/" + id + "/comments/save' page");

        return "comment/saveTaskComment";
    }

    @PostMapping("/{id}/comments/save")
    public String saveComment(@Valid @ModelAttribute("form") TaskCommentForm form, @PathVariable Long id) {
        User currentUser = userService.getCurrentUser();
        TaskComment comment = conversionService.convert(form, TaskComment.class);
        usersTaskService.saveTaskComment(comment);

        logger.info("User " + currentUser + " added task comment successfully");

        return String.format("redirect:/tasks/%d", id);
    }
}
