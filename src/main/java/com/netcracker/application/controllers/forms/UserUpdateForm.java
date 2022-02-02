package com.netcracker.application.controllers.forms;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class UserUpdateForm {
    @NotBlank(message = "{registration.username.blank}")
    @Size(max = 50, message = "{registration.username.long}")
    private String name;
    private CharSequence password;
    private CharSequence confirmPassword;
    private CharSequence oldPassword;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CharSequence getPassword() {
        return password;
    }

    public void setPassword(CharSequence password) {
        this.password = password;
    }

    public CharSequence getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(CharSequence confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public CharSequence getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(CharSequence oldPassword) {
        this.oldPassword = oldPassword;
    }

    @Override
    public String toString() {
        return "UserUpdateForm{" +
                "name='" + name + '\'' +
                '}';
    }
}
