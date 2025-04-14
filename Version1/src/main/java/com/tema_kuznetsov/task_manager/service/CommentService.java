package com.tema_kuznetsov.task_manager.service;

import com.tema_kuznetsov.task_manager.dto.commentDto.CommentCreateDto;
import com.tema_kuznetsov.task_manager.dto.commentDto.CommentResponseDto;
import com.tema_kuznetsov.task_manager.dto.commentDto.CommentUpdateDto;
import com.tema_kuznetsov.task_manager.dto.taskDto.TaskResponseDto;
import com.tema_kuznetsov.task_manager.exception.commentException.CommentIdNotFoundException;
import com.tema_kuznetsov.task_manager.exception.commentException.CommentTextLengthExceededException;
import com.tema_kuznetsov.task_manager.exception.taskException.TaskIdNotFoundException;
import com.tema_kuznetsov.task_manager.exception.userException.UserIdNotFoundException;
import com.tema_kuznetsov.task_manager.model.Comment;
import com.tema_kuznetsov.task_manager.model.Task;
import com.tema_kuznetsov.task_manager.model.constrain.CommentConstrains;
import com.tema_kuznetsov.task_manager.repository.CommentRepository;
import com.tema_kuznetsov.task_manager.repository.TaskRepository;
import com.tema_kuznetsov.task_manager.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor // конструктор только для файнал полей
@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    //1. Создание комментария
    @Transactional
    public Comment createComment(CommentCreateDto dto) {
        Comment comment = new Comment();
        comment.setText(dto.getText());

        comment.setAuthor(userRepository.findById(dto.getUser_id())
                .orElseThrow(() -> new UserIdNotFoundException(dto.getUser_id())));

        comment.setTask(taskRepository.findById(dto.getTask_id())
                .orElseThrow(() -> new TaskIdNotFoundException(dto.getTask_id())));

        return commentRepository.save(comment);
    }

    //2. Получение комментария по айди
    public CommentResponseDto findCommentById(Long id) {
        Comment comment = getCommentByIdOrThrow(id);
        return new CommentResponseDto(comment);
    }

    //3. Удаление комментария по айди
    @Transactional
    public void deleteCommentById(Long id) {
        Comment comment = getCommentByIdOrThrow(id);
       commentRepository.delete(comment);
    }

    //4. Обновление комментария по айди
    @Transactional
    public CommentResponseDto updateCommentById(Long id, CommentUpdateDto dto) {
        Comment comment = getCommentByIdOrThrow(id);

        if (dto.getText().length() > CommentConstrains.MAX_TEXT_LENGTH) {
         throw new CommentTextLengthExceededException(CommentConstrains.MAX_TEXT_LENGTH);
        }

        comment.setText(dto.getText());

       return new CommentResponseDto(comment);
    }

    // Вспомогательный метод для получения комментов по айди их задачи
    public Page<CommentResponseDto> getCommentsForTask(Long taskId, Pageable pageable) {
        return commentRepository.findByTaskIdOrderByCreatedAtDesc(taskId,pageable)
                .map(CommentResponseDto::new);
    }

    // Вспомогательный метод для получения комментов принадлежащих ползователю по айди
    public Page <CommentResponseDto> getCommentsForUser(Long userId, Pageable pageable) {
        return commentRepository.findByAuthorIdOrderByCreatedAtDesc(userId,pageable)
                .map(CommentResponseDto::new);
    }

    // Вспомогательный метод для поиска пользователя по айди + валидация
    public Comment getCommentByIdOrThrow(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new CommentIdNotFoundException(id));
    }
}
