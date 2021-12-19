package com.netcracker.application.controllers;

import com.netcracker.application.controllers.forms.UserRegistrationForm;
import com.netcracker.application.controllers.validators.UserRegistrationValidator;
import com.netcracker.application.services.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
public class AuthController {

    private final UserServiceImpl userService;
    private final UserRegistrationValidator userRegistrationValidator;

    @Autowired
    public AuthController(UserRegistrationValidator userRegistrationValidator, UserServiceImpl userService) {
        this.userRegistrationValidator = userRegistrationValidator;
        this.userService = userService;
    }

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.addValidators(userRegistrationValidator);
    }

    @GetMapping("/registration")
    public String registration(Model model) {
        model.addAttribute("form", new UserRegistrationForm());
        return "auth/registration";
    }

    @GetMapping("/login")
    public String login(Model model, String error, String logout) {
        if (Objects.nonNull(error))
            model.addAttribute("error", "User name or password is incorrect");

        if (Objects.nonNull(logout))
            model.addAttribute("message", "Logout successful");

        return "auth/login";
    }

    @PostMapping("/registration")
    public String registration(
            @Valid @ModelAttribute("form") UserRegistrationForm userRegistrationForm,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors()
                    .stream().map(ObjectError::getDefaultMessage)
                    .collect(Collectors.toList());
            model.addAttribute("errors", errorMessages);
            return "auth/registration";
        }

        userService.createUser(userRegistrationForm);
        return "redirect:/";
    }
}
