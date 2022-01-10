package com.netcracker.application.controllers;

import com.netcracker.application.model.Statistic;
import com.netcracker.application.model.UsersTask;
import com.netcracker.application.services.UsersTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/profile/tasks/statistics")
public class StatisticsController {

    private final UsersTaskService usersTaskService;

    @Autowired
    public StatisticsController(UsersTaskService usersTaskService) {
        this.usersTaskService = usersTaskService;
    }

    @GetMapping("/{id}")
    public String getStatisticsForTask(@PathVariable Long id, Model model) {
        UsersTask usersTaskById = usersTaskService.getUsersTaskById(id);
        List<Statistic> statisticsForUsersTask = usersTaskService
                .getStatisticsForUsersTask(usersTaskById, LocalDate.MIN, LocalDate.MAX);

        model.addAttribute("task", usersTaskById);
        model.addAttribute("statistics", statisticsForUsersTask);

        return "statistics/taskStatistics";
    }
}
