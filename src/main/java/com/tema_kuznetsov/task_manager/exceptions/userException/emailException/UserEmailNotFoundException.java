package com.tema_kuznetsov.task_manager.exceptions.userException.emailException;

public class UserEmailNotFoundException extends RuntimeException {
    public UserEmailNotFoundException(String email) {
      super("User not found with email: " + email);
    }
}
