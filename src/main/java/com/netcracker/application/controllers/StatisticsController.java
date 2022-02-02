package com.netcracker.application.controllers;

import com.netcracker.application.model.Statistic;
import com.netcracker.application.model.User;
import com.netcracker.application.model.UsersTask;
import com.netcracker.application.services.UserService;
import com.netcracker.application.services.UsersTaskService;
import com.netcracker.logging.LogManager;
import com.netcracker.logging.loggers.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/tasks/statistics")
public class StatisticsController {
    private final Logger logger = LogManager.getLogger("main.java", StatisticsController.class);
    private final String DATE_INPUT_FORMAT = "yyyy-MM-dd";

    private final UserService userService;
    private final UsersTaskService usersTaskService;

    @Autowired
    public StatisticsController(UsersTaskService usersTaskService, UserService userService) {
        this.usersTaskService = usersTaskService;
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public String getStatisticsForTask(@PathVariable Long id, String start, String end, Model model) {
        UsersTask usersTaskById = usersTaskService.getUsersTaskById(id);
        User currentUser = userService.getCurrentUser();

        LocalDate startDate = LocalDate.MIN;
        LocalDate endDate = LocalDate.MAX;

        if (Objects.nonNull(start) && !start.isEmpty()) {
            try {
                startDate = LocalDate.parse(start, DateTimeFormatter.ofPattern(DATE_INPUT_FORMAT));
            } catch (IllegalArgumentException ignore) {
            }
        }
        if (Objects.nonNull(end) && !end.isEmpty()) {
            try {
                endDate = LocalDate.parse(end, DateTimeFormatter.ofPattern(DATE_INPUT_FORMAT));
            } catch (IllegalArgumentException ignore) {
            }
        }

        List<Statistic> statisticsForUsersTask = usersTaskService
                .getStatisticsForUsersTask(usersTaskById, startDate, endDate);
        List<Statistic> monthlyStatisticsForUsersTask = usersTaskService
                .getMonthlyStatisticsForUsersTask(usersTaskById, startDate, endDate);

        model.addAttribute("task", usersTaskById);
        model.addAttribute("statistics", statisticsForUsersTask);
        model.addAttribute("monthlyStatistics", monthlyStatisticsForUsersTask);
        model.addAttribute("start", start);
        model.addAttribute("end", end);

        logger.info("User " + currentUser + " accessed '/tasks/statistics/" + id + "' page");

        return "statistics/taskStatistics";
    }
}
