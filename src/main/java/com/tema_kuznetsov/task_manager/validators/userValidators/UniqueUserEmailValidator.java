package com.tema_kuznetsov.task_manager.validators.userValidators;

import com.tema_kuznetsov.task_manager.annotations.userAnnotations.UniqueUserEmail;
import com.tema_kuznetsov.task_manager.repositories.UserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class UniqueUserEmailValidator implements ConstraintValidator<UniqueUserEmail, String> {
    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null || email.isBlank()) {
            return true;
        }
        return  !userRepository.existsByEmail(email);
    }
}
