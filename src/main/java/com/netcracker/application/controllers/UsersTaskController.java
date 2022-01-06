package com.netcracker.application.controllers;

import com.netcracker.application.controllers.forms.UserRegistrationForm;
import com.netcracker.application.controllers.forms.UsersTaskForm;
import com.netcracker.application.controllers.validators.UsersTaskFormValidator;
import com.netcracker.application.model.Task;
import com.netcracker.application.model.User;
import com.netcracker.application.model.UsersTask;
import com.netcracker.application.services.UserService;
import com.netcracker.application.services.UsersTaskService;
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
@RequestMapping("/profile/tasks")
public class UsersTaskController {

    private final UserService userService;
    private final UsersTaskService usersTaskService;
    private UsersTaskFormValidator usersTaskFormValidator;
    private ConversionService conversionService;

    @Autowired
    public UsersTaskController(UserService userService, UsersTaskService usersTaskService) {
        this.userService = userService;
        this.usersTaskService = usersTaskService;
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
        if (!currentUser.getId().equals(usersTask.getUser().getId()))
            throw new AccessDeniedException("");

        model.addAttribute("user", currentUser);
        model.addAttribute("task", usersTask);
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
        }
        else {
            UsersTask task = usersTaskService.getUsersTaskById(id);
            form = conversionService.convert(task, UsersTaskForm.class);
        }

        model.addAttribute("tasks", tasks);
        model.addAttribute("form", form);
        return "task/saveUsersTask";
    }

    @PostMapping("/save")
    public String saveUsersTask(
            @Valid @ModelAttribute("form") UsersTaskForm form,
            BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList());
            model.addAttribute("errors", errorMessages);
            return "task/saveUsersTask";
        }

        usersTaskService.saveUsersTask(conversionService.convert(form, UsersTask.class));
        return "redirect:/profile";
    }

    @PostMapping("/delete")
    public String deleteUsersTask(@RequestParam("task") Long id) {
        User currentUser = userService.getCurrentUser();
        UsersTask task = usersTaskService.getUsersTaskById(id);
        if (!task.getUser().getId().equals(currentUser.getId()))
            throw new AccessDeniedException("Invalid access to a task");

        usersTaskService.deleteUsersTaskById(id);

        return "redirect:/profile";
    }

    @GetMapping("/active")
    public String getActiveTask() {
        User currentUser = userService.getCurrentUser();
        List<UsersTask> userTasks = usersTaskService.getUsersTasksByUserId(currentUser.getId());
        UsersTask activeTask = userTasks.stream()
                .filter(task ->
                        task.getUsages().stream().anyMatch(usage -> Objects.isNull(usage.getEndTime()))
                )
                .findFirst().orElseThrow(IllegalStateException::new);

        return String.format("redirect:/profile/tasks/%d", activeTask.getId());
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
        return "redirect:/profile";
    }
}
