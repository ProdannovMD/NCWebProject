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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/tasks/statistics")
public class StatisticsController {
    private final String DATE_INPUT_FORMAT = "yyyy-MM-dd";

    private final UsersTaskService usersTaskService;

    @Autowired
    public StatisticsController(UsersTaskService usersTaskService) {
        this.usersTaskService = usersTaskService;
    }

    @GetMapping("/{id}")
    public String getStatisticsForTask(@PathVariable Long id, String start, String end, Model model) {
        UsersTask usersTaskById = usersTaskService.getUsersTaskById(id);

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

        return "statistics/taskStatistics";
    }
}
