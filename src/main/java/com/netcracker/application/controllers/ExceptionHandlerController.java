package com.netcracker.application.controllers;

import com.netcracker.application.controllers.exceptions.ResourceNotFoundException;
import com.netcracker.logging.LogManager;
import com.netcracker.logging.loggers.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Objects;

@ControllerAdvice
public class ExceptionHandlerController {
    private final Logger logger = LogManager.getLogger("main.java", ExceptionHandlerController.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String resourceNotFound(ResourceNotFoundException ex, Model model) {
        if (Objects.nonNull(ex.getMessage()))
            model.addAttribute("message", ex.getMessage());

        logger.error(ex);

        return "error/404";
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String accessDenied(AccessDeniedException ex, Model model) {
        if (Objects.nonNull(ex.getMessage()))
            model.addAttribute("message", ex.getMessage());

        logger.error(ex);

        return "error/403";
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String internalServerError(RuntimeException ex, Model model) {
        ex.printStackTrace();
        if (Objects.nonNull(ex.getMessage()))
            model.addAttribute("message", ex.getMessage());

        logger.error(ex);

        return "error/500";
    }


}
