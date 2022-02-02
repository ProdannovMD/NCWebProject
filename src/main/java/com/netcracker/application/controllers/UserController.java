package com.netcracker.application.controllers;

import com.netcracker.application.controllers.forms.UserUpdateForm;
import com.netcracker.application.controllers.validators.UserUpdateFormValidator;
import com.netcracker.application.model.User;
import com.netcracker.application.model.UsersTask;
import com.netcracker.application.services.UserService;
import com.netcracker.application.services.UsersTaskService;
import com.netcracker.logging.LogManager;
import com.netcracker.logging.loggers.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/profile")
public class UserController {
    private final Logger logger = LogManager.getLogger("main.java", UserController.class);
    private final UserService userService;
    private final UsersTaskService usersTaskService;

    private ConversionService conversionService;
    private UserUpdateFormValidator updateFormValidator;

    @Autowired
    public UserController(UserService userService, UsersTaskService usersTaskService) {
        this.userService = userService;
        this.usersTaskService = usersTaskService;
    }

    @Autowired
    public void setConversionService(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @Autowired
    public void setUpdateFormValidator(UserUpdateFormValidator updateFormValidator) {
        this.updateFormValidator = updateFormValidator;
    }

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        Optional<Object> userTaskTarget = Optional.ofNullable(binder.getTarget())
                .filter(field -> field.getClass().equals(UserUpdateForm.class));
        if (userTaskTarget.isPresent())
            binder.setValidator(updateFormValidator);
    }

    @GetMapping
    public String profile(Model model) {
        User currentUser = userService.getCurrentUser();
        List<UsersTask> userTasks = usersTaskService.getUsersTasksByUserId(currentUser.getId());

        model.addAttribute("user", currentUser);
        model.addAttribute("tasks", userTasks);

        logger.info("User " + currentUser + " accessed '/profile' page");

        return "user/profile";
    }

    @GetMapping("/edit")
    public String editProfile(Model model) {
        User currentUser = userService.getCurrentUser();
        UserUpdateForm form = conversionService.convert(currentUser, UserUpdateForm.class);
        model.addAttribute("form", form);

        logger.info("User " + currentUser + " accessed '/profile/edit' page");

        return "user/edit";
    }

    @PostMapping("/edit")
    public String editUser(
            @Valid @ModelAttribute("form") UserUpdateForm form,
            BindingResult bindingResult, Model model
    ) {
        User currentUser = userService.getCurrentUser();
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList());
            model.addAttribute("errors", errorMessages);

            logger.info("Profile update by user " + currentUser + " failed with errors: " + errorMessages);

            return "user/edit";
        }
        User user = conversionService.convert(form, User.class);
        userService.updateUser(user);
        userService.logoutUser();

        model.addAttribute("message", "Edit successful. You can login with new credentials");

        logger.info("Profile update by user " + currentUser + " successful");

        return "auth/login";
    }

}
