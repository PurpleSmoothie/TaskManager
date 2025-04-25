package com.tema_kuznetsov.task_manager.models.constrains;

import lombok.Getter;

@Getter
public final class TaskConstrains {
    public static final int MAX_TITLE_LENGTH = 100;
    public static final int MIN_TITLE_LENGTH = 5;
    public static final int MAX_DESC_LENGTH = 500;
    public static final int MIN_DESC_LENGTH = 5;

    private TaskConstrains() {}
}