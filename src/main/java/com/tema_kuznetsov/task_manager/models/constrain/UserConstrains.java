package com.tema_kuznetsov.task_manager.models.constrain;

import lombok.Getter;

@Getter
public final class UserConstrains {
    public static final int MAX_LOGIN_LENGTH = 30;
    public static final int MAX_PASSWORD_LENGTH = 100;
    public static final int MAX_EMAIL_LENGTH = 100;
    public static final int MIN_LOGIN_LENGTH = 4;
    public static final int MIN_PASSWORD_LENGTH = 8;
    public static final int MIN_EMAIL_LENGTH = 5;

    private UserConstrains() {} // Запрещаем создание экземпляров
}
