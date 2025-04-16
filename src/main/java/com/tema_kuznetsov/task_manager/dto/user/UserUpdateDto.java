package com.tema_kuznetsov.task_manager.dto.user;

import com.tema_kuznetsov.task_manager.annotation.userAnnotations.UniqueUserEmail;
import com.tema_kuznetsov.task_manager.annotation.userAnnotations.UniqueUserLogin;
import com.tema_kuznetsov.task_manager.annotation.userAnnotations.ValidPassword;
import com.tema_kuznetsov.task_manager.model.constrain.UserConstrains;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateDto {

    @Size(min = UserConstrains.MIN_LOGIN_LENGTH,max = UserConstrains.MAX_LOGIN_LENGTH)
    @UniqueUserLogin
    private String login;

    @Email
    @Size(min = UserConstrains.MIN_EMAIL_LENGTH,max = UserConstrains.MAX_EMAIL_LENGTH)
    @UniqueUserEmail
    private String email;

    @ValidPassword
    @Size(min = UserConstrains.MIN_PASSWORD_LENGTH,max = UserConstrains.MAX_PASSWORD_LENGTH)
    private String password;
}
