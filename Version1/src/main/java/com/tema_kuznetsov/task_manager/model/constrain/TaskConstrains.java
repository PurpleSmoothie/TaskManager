package com.tema_kuznetsov.task_manager.model.constrain;

import lombok.Getter;

@Getter
public final class TaskConstrains {
    public static final int MAX_TITLE_LENGTH = 100;
    public static final int MAX_DESC_LENGTH = 500;

    private TaskConstrains() {} // Запрещаем создание экземпляров
}