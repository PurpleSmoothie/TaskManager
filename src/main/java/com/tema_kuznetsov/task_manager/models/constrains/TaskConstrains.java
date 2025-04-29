package com.tema_kuznetsov.task_manager.models.constrains;

import lombok.Getter;

/**
 * Класс, содержащий ограничения для задач.
 * Включает максимальные и минимальные длины для заголовка и описания задачи.
 * Этот класс не предназначен для создания экземпляров.
 */
@Getter
public final class TaskConstrains {

    /**
     * Максимальная длина заголовка задачи.
     */
    public static final int MAX_TITLE_LENGTH = 100;

    /**
     * Минимальная длина заголовка задачи.
     */
    public static final int MIN_TITLE_LENGTH = 5;

    /**
     * Максимальная длина описания задачи.
     */
    public static final int MAX_DESC_LENGTH = 500;

    /**
     * Минимальная длина описания задачи.
     */
    public static final int MIN_DESC_LENGTH = 5;

    /**
     * Приватный конструктор для предотвращения создания экземпляров класса.
     */
    private TaskConstrains() {}
}