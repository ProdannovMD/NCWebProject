package com.netcracker.application.controllers.forms;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class UserRegistrationForm {
    @NotBlank(message = "User name must not be blank")
    @Size(max = 50, message = "User name should be under 50 characters")
    private String name;
    @Size(min = 5, message = "Password length must be more than 5 characters")
    private CharSequence password;
    private CharSequence confirmPassword;

    public UserRegistrationForm() {
    }

    public UserRegistrationForm(String name, String password, String confirmPassword) {
        this.name = name;
        this.password = password;
        this.confirmPassword = confirmPassword;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public @Size(min = 5, message = "Password length must be more than 5 characters") CharSequence getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public CharSequence getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    @Override
    public String toString() {
        return "UserRegistrationForm{" +
                "name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", confirmPassword='" + confirmPassword + '\'' +
                '}';
    }
}
