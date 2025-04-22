package com.tema_kuznetsov.task_manager.models.constrain;

import lombok.Getter;

@Getter
public class CommentConstrains {
    public static final int MAX_TEXT_LENGTH = 1000;
    private UserConstrains userConstrains; // запрещаем созданием экземпляров
}
