package com.tema_kuznetsov.task_manager.validators.userValidators;

import com.tema_kuznetsov.task_manager.annotations.userAnnotations.UniqueUserEmail;
import com.tema_kuznetsov.task_manager.repositories.UserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Валидатор для аннотации UniqueUserEmail, проверяет, что email пользователя уникален в системе.
 */
public class UniqueUserEmailValidator implements ConstraintValidator<UniqueUserEmail, String> {
    @Autowired
    private UserRepository userRepository;

    /**
     * Проверяет, что email пользователя уникален в системе.
     *
     * @param email   Email пользователя.
     * @param context Контекст валидатора.
     * @return true, если email уникален, иначе false.
     */
    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null || email.isBlank()) {
            return true;
        }
        return  !userRepository.existsByEmail(email);
    }
}