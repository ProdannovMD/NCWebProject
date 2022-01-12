package com.netcracker.application.controllers;

import com.netcracker.application.controllers.forms.AssignTaskForm;
import com.netcracker.application.model.User;
import com.netcracker.application.model.UsersTask;
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
    private ConversionService conversionService;

    @Autowired
    public AdminController(UserService userService, UsersTaskService usersTaskService) {
        this.userService = userService;
        this.usersTaskService = usersTaskService;
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
//        UsersTask activeTask = userTasks.stream()
//                .filter(UsersTask::isActive)
//                .findFirst().orElseThrow(IllegalStateException::new);

        model.addAttribute("user", user);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("tasks", userTasks);
//        model.addAttribute("activeTask", activeTask);

        return "user/profile";
    }

    @GetMapping("/users/{id}/assign")
    public String assignTaskForm(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id);
        User currentUser = userService.getCurrentUser();
        List<UsersTask> usersTasks = usersTaskService.getUsersTasksByUserId(user.getId());
        List<UsersTask> currentUsersTasks = usersTaskService.getUsersTasksByUserId(currentUser.getId());
        currentUsersTasks = currentUsersTasks.stream()
                .filter(
                        task -> task.getTask().getCreatedBy().getId().equals(currentUser.getId()) &&
                                usersTasks.stream().noneMatch(ut -> ut.getTask().getId().equals(task.getId()))
                )
                .collect(Collectors.toList());


        AssignTaskForm form = new AssignTaskForm();
        form.setUser(id);

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
}
