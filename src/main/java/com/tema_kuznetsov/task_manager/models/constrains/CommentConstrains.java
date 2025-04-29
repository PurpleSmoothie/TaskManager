package com.tema_kuznetsov.task_manager.models.constrains;

import lombok.Getter;

/**
 * Класс, содержащий ограничения для комментариев.
 * Включает максимальную длину текста комментария.
 * Этот класс не предназначен для создания экземпляров.
 */
@Getter
public class CommentConstrains {

    /**
     * Максимальная длина текста комментария.
     */
    public static final int MAX_TEXT_LENGTH = 1000;

    /**
     * Приватный конструктор для предотвращения создания экземпляров класса.
     */
    private CommentConstrains() {}
}
