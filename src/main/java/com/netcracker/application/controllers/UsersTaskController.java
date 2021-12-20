package com.netcracker.application.controllers;

import com.netcracker.application.controllers.forms.UsersTaskForm;
import com.netcracker.application.model.Task;
import com.netcracker.application.model.User;
import com.netcracker.application.model.UsersTask;
import com.netcracker.application.services.UserService;
import com.netcracker.application.services.UsersTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/profile")
public class UsersTaskController {

    private final UserService userService;
    private final UsersTaskService usersTaskService;

    @Autowired
    public UsersTaskController(UserService userService, UsersTaskService usersTaskService) {
        this.userService = userService;
        this.usersTaskService = usersTaskService;
    }

    @GetMapping("/tasks/{id}")
    public String getUsersTask(@PathVariable Long id, Model model) throws IllegalAccessException {
        User currentUser = userService.getCurrentUser();
        UsersTask usersTask = usersTaskService.getUsersTaskById(id);
        if (!currentUser.getId().equals(usersTask.getUser().getId()))
            throw new IllegalAccessException();

        model.addAttribute("user", currentUser);
        model.addAttribute("task", usersTask);
        return "task/usersTask";
    }

    @GetMapping("/tasks")
    public String newUsersTaskForm(Model model) {
        User currentUser = userService.getCurrentUser();
        List<UsersTask> tasks = usersTaskService.getUsersTasksByUserId(currentUser.getId());
        System.out.println(tasks);

        model.addAttribute("tasks", tasks);
        model.addAttribute("form", new UsersTaskForm());
        return "task/newUsersTask";
    }

    @PostMapping("/tasks")
    public String newUsersTask(
            @Valid @ModelAttribute("form") UsersTaskForm form,
            BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList());
            model.addAttribute("errors", errorMessages);
            return "task/newUsersTask";
        }
        User currentUser = userService.getCurrentUser();
        Task newTask = new Task();
        newTask.setName(form.getName());

        UsersTask usersTask = new UsersTask();
        usersTask.setTask(newTask);
        usersTask.setUser(currentUser);
        if (form.getParent() > 0) {
            UsersTask parentTask = usersTaskService.getUsersTaskById(form.getParent());
            usersTask.setParentTask(parentTask);
        }
        usersTaskService.saveUsersTask(usersTask);
        return "redirect:/profile";
    }

    @GetMapping
    public String profile(Model model) {
        User currentUser = userService.getCurrentUser();
        List<UsersTask> userTasks = usersTaskService.getUsersTasksByUserId(currentUser.getId());
        UsersTask activeTask = userTasks.stream()
                .filter(task ->
                        task.getUsages().stream().anyMatch(usage -> Objects.isNull(usage.getEndTime()))
                )
                .findFirst().orElseThrow(IllegalStateException::new);

        model.addAttribute("user", currentUser);
        model.addAttribute("tasks", userTasks);
        model.addAttribute("activeTask", activeTask);
        return "user/profile";
    }

    @GetMapping("/tasks/active")
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
}
