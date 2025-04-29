package com.tema_kuznetsov.task_manager.validators.userValidators;

import com.tema_kuznetsov.task_manager.annotations.userAnnotations.UniqueUserLogin;
import com.tema_kuznetsov.task_manager.repositories.UserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Валидатор для аннотации UniqueUserLogin, проверяет, что логин пользователя уникален в системе.
 */
public class UniqueUserLoginValidator implements ConstraintValidator<UniqueUserLogin, String> {
    @Autowired
    private UserRepository userRepository;

    /**
     * Проверяет, что логин пользователя уникален в системе.
     *
     * @param login   Логин пользователя.
     * @param context Контекст валидатора.
     * @return true, если логин уникален, иначе false.
     */
    @Override
    public boolean isValid(String login, ConstraintValidatorContext context) {
        if (login == null || login.isBlank()) {
            return true;
        }
        return  !userRepository.existsByLogin(login);
    }
}