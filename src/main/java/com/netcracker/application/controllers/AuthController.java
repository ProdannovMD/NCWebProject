package com.netcracker.application.controllers;

import com.netcracker.application.controllers.forms.UserRegistrationForm;
import com.netcracker.application.controllers.validators.UserRegistrationFormValidator;
import com.netcracker.application.model.User;
import com.netcracker.application.services.impl.UserServiceImpl;
import com.netcracker.logging.LogManager;
import com.netcracker.logging.loggers.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
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
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class AuthController {
    private final Logger logger = LogManager.getLogger("main.java", AuthController.class);
    private final UserServiceImpl userService;
    private final UserRegistrationFormValidator userRegistrationValidator;
    private ConversionService conversionService;

    @Autowired
    public AuthController(UserRegistrationFormValidator userRegistrationValidator, UserServiceImpl userService) {
        this.userRegistrationValidator = userRegistrationValidator;
        this.userService = userService;
    }

    @Autowired
    public void setConversionService(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        Optional<Object> userRegistrationTarget = Optional.ofNullable(binder.getTarget())
                .filter(field -> field.getClass().equals(UserRegistrationForm.class));
        if (userRegistrationTarget.isPresent())
            binder.setValidator(userRegistrationValidator);
    }

    @GetMapping("/registration")
    public String registration(Model model) {
        model.addAttribute("form", new UserRegistrationForm());

        logger.info("'/registration' page has been accessed");

        return "auth/registration";
    }

    @GetMapping("/login")
    public String login(Model model, String error, String logout) {
        if (Objects.nonNull(error))
            model.addAttribute("error", "User name or password is incorrect");

        if (Objects.nonNull(logout))
            model.addAttribute("message", "Logout successful");

        logger.info("'/login' page has been accessed");

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
            logger.info("Registration failed with errors: " + errorMessages);
            return "auth/registration";
        }

        userService.createUser(conversionService.convert(userRegistrationForm, User.class));
        logger.info("Registration has been successful");
        return "redirect:/";
    }
}
