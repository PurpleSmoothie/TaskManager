package com.tema_kuznetsov.task_manager.validator.userValidator;

import com.tema_kuznetsov.task_manager.annotation.userAnnotations.UniqueUserEmail;
import com.tema_kuznetsov.task_manager.repository.UserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class UniqueUserEmailValidator implements ConstraintValidator<UniqueUserEmail, String> {
    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null || email.isBlank()) {
            return true; // Пропускаем, это проверяет @NotBlank
        }
        // Проверка уникальности только для непустых значений
        return  !userRepository.existsByEmail(email);
    }
}
