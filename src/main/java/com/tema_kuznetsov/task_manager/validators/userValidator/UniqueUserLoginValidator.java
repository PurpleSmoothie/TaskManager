package com.tema_kuznetsov.task_manager.validators.userValidator;

import com.tema_kuznetsov.task_manager.annotation.userAnnotations.UniqueUserLogin;
import com.tema_kuznetsov.task_manager.repositories.UserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class UniqueUserLoginValidator implements ConstraintValidator<UniqueUserLogin, String> {
    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean isValid(String login, ConstraintValidatorContext context) {
        if (login == null || login.isBlank()) {
            return true; // Пропускаем, это проверяет @NotBlank
        }
        // Проверка уникальности только для непустых значений
        return  !userRepository.existsByLogin(login);
    }
}
