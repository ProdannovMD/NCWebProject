package com.netcracker.application.controllers;

import com.netcracker.application.controllers.forms.AssignTaskForm;
import com.netcracker.application.controllers.forms.StatusForm;
import com.netcracker.application.model.Status;
import com.netcracker.application.model.TaskComment;
import com.netcracker.application.model.User;
import com.netcracker.application.model.UsersTask;
import com.netcracker.application.services.StatusService;
import com.netcracker.application.services.UserService;
import com.netcracker.application.services.UsersTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminController {
    private final UserService userService;
    private final UsersTaskService usersTaskService;
    private final StatusService statusService;
    private ConversionService conversionService;

    @Autowired
    public AdminController(UserService userService, UsersTaskService usersTaskService, StatusService statusService) {
        this.userService = userService;
        this.usersTaskService = usersTaskService;
        this.statusService = statusService;
    }

    @Autowired
    public void setConversionService(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @GetMapping
    public String admin() {
        return "redirect:/admin/users";
    }

    @GetMapping("/users")
    public String getUsers(Model model) {
        List<User> users = userService.getAllUsers();
        User currentUser = userService.getCurrentUser();

        model.addAttribute("users", users);
        model.addAttribute("currentUser", currentUser);

        return "user/users";
    }

    @GetMapping("/users/{id}")
    public String getUserProfile(@PathVariable Long id, Model model) {
        User currentUser = userService.getCurrentUser();
        User user = userService.getUserById(id);
        List<UsersTask> userTasks = usersTaskService.getUsersTasksByUserId(user.getId());

        model.addAttribute("user", user);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("tasks", userTasks);

        return "user/profile";
    }

    @GetMapping("/users/{id}/assign")
    public String assignTaskForm(@PathVariable Long id, Long parent, Model model) {
        User user = userService.getUserById(id);
        User currentUser = userService.getCurrentUser();
        List<UsersTask> usersTasks = usersTaskService.getUsersTasksByUserId(user.getId());
        List<UsersTask> currentUsersTasks = usersTaskService.getUsersTasksByUserId(currentUser.getId());
        currentUsersTasks = currentUsersTasks.stream()
                .filter(
                        task -> task.getTask().getCreatedBy().getId().equals(currentUser.getId()) &&
                                usersTasks.stream().noneMatch(ut -> ut.getTask().getId().equals(task.getTask().getId()))
                )
                .collect(Collectors.toList());


        AssignTaskForm form = new AssignTaskForm();
        form.setUser(id);

        model.addAttribute("parents", usersTasks);
        model.addAttribute("tasks", currentUsersTasks);
        model.addAttribute("user", user);
        model.addAttribute("form", form);

        return "task/assignTask";
    }

    @PostMapping("/users/{id}/assign")
    public String assignTask(@PathVariable Long id, @ModelAttribute("form") AssignTaskForm form) {
        UsersTask usersTask = conversionService.convert(form, UsersTask.class);
        usersTaskService.saveUsersTask(usersTask);

        return String.format("redirect:/admin/users/%d", id);
    }

    @GetMapping("/users/{id}/tasks/{task}")
    public String getUsersTask(@PathVariable Long id, @PathVariable Long task, Model model) {
        User user = userService.getUserById(id);
        User currentUser = userService.getCurrentUser();
        UsersTask usersTask = usersTaskService.getUsersTaskById(task);
        List<TaskComment> comments = usersTaskService.getTaskCommentsForUsersTask(usersTask);
        List<Status> statuses = statusService.getAllStatuses();
        StatusForm form = new StatusForm();
        form.setStatus(usersTask.getTask().getStatus().getId());
        form.setTask(usersTask.getId());

        model.addAttribute("comments", comments);
        model.addAttribute("user", user);
        model.addAttribute("task", usersTask);
        model.addAttribute("statuses", statuses);
        model.addAttribute("form", form);
        model.addAttribute("currentUser", currentUser);

        return "task/usersTask";
    }
}
