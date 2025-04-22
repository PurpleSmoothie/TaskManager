package com.tema_kuznetsov.task_manager.validators.userValidator;

import com.tema_kuznetsov.task_manager.annotation.userAnnotations.ValidPassword;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UserPasswordValidator implements ConstraintValidator<ValidPassword, String> {

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null || password.isBlank()) {
            return true; // Пропускаем, это проверяет @NotBlank
        }
        return password.matches(".*[a-zA-Z].*"); // Проверяем наличие хотя бы одной буквы
    }
}


