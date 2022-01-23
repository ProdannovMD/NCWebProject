package com.netcracker.application.controllers;

import com.netcracker.application.controllers.forms.AssignTaskForm;
import com.netcracker.application.controllers.forms.StatusForm;
import com.netcracker.application.model.*;
import com.netcracker.application.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminController {
    private final UserService userService;
    private final UsersTaskService usersTaskService;
    private final TaskService taskService;
    private final StatusService statusService;
    private final TaskHistoryService taskHistoryService;
    private ConversionService conversionService;

    @Autowired
    public AdminController(
            UserService userService,
            UsersTaskService usersTaskService,
            TaskService taskService,
            StatusService statusService,
            TaskHistoryService taskHistoryService
    ) {
        this.userService = userService;
        this.usersTaskService = usersTaskService;
        this.taskService = taskService;
        this.statusService = statusService;
        this.taskHistoryService = taskHistoryService;
    }

    @Autowired
    public void setConversionService(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @GetMapping
    public String admin() {
        return "admin/administration";
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
        if (currentUser.getId().equals(id))
            return "redirect:/profile";

        User user = userService.getUserById(id);
        List<UsersTask> userTasks = usersTaskService.getUsersTasksByUserId(user.getId());

        model.addAttribute("user", user);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("tasks", userTasks);

        return "user/profile";
    }

    @GetMapping("/users/{id}/assign")
    public String assignTaskForm(@PathVariable Long id, Long parent, Model model) {
        User currentUser = userService.getCurrentUser();
        if (currentUser.getId().equals(id))
            return "redirect:/profile";

        User user = userService.getUserById(id);
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
        User currentUser = userService.getCurrentUser();
        if (currentUser.getId().equals(id))
            return String.format("redirect:/profile/tasks/%d", task);

        User user = userService.getUserById(id);
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

    @GetMapping("/tasks/history")
    public String getTaskHistory(Long task, Long user, Model model) {
        List<TaskHistory> history = taskHistoryService.getAllTaskHistory();
        if (Objects.nonNull(task))
            history = history.stream().filter(h -> h.getTask().getId().equals(task)).collect(Collectors.toList());
        if (Objects.nonNull(user))
            history = history.stream().filter(h -> h.getUser().getId().equals(user)).collect(Collectors.toList());

        Collections.reverse(history);

        model.addAttribute("history", history);

        return "history/taskHistory";
    }

    @GetMapping("/tasks")
    public String getAllTasks(Model model) {
        List<Task> tasks = taskService.getAllTasks();

        model.addAttribute("tasks", tasks);

        return "task/tasks";
    }

    @GetMapping("/tasks/{id}/statistics")
    public String getTaskStatistics(@PathVariable Long id, LocalDate start, LocalDate end, Model model) {
        Task task = taskService.getTaskById(id);
        if (Objects.isNull(start))
            start = LocalDate.MIN;
        if (Objects.isNull(end))
            end = LocalDate.MAX;
        List<Statistic> statistics = usersTaskService.getStatisticsForTask(task, start, end);

        model.addAttribute("task", task);
        model.addAttribute("statistics", statistics);

        return "statistics/adminStatistics";
    }

    @GetMapping("/tasks/{id}")
    public String getTask(@PathVariable Long id, Model model) {
        Task task = taskService.getTaskById(id);
        List<UsersTask> usersTasks = usersTaskService.getUsersTasksByTask(task);
        List<TaskComment> comments = usersTaskService.getTaskCommentsForTask(task);

        model.addAttribute("task", task);
        model.addAttribute("usersTasks", usersTasks);
        model.addAttribute("comments", comments);

        return "task/task";
    }
}
