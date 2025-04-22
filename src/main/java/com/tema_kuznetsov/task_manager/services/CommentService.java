package com.tema_kuznetsov.task_manager.services;

import com.tema_kuznetsov.task_manager.dto.comment.CommentCreateDto;
import com.tema_kuznetsov.task_manager.dto.comment.CommentResponseDto;
import com.tema_kuznetsov.task_manager.dto.comment.CommentUpdateDto;
import com.tema_kuznetsov.task_manager.exceptions.commentException.CommentIdNotFoundException;
import com.tema_kuznetsov.task_manager.exceptions.commentException.CommentTextLengthExceededException;
import com.tema_kuznetsov.task_manager.exceptions.taskException.TaskIdNotFoundException;
import com.tema_kuznetsov.task_manager.exceptions.userException.emailException.UserEmailNotFoundException;
import com.tema_kuznetsov.task_manager.models.AppUser;
import com.tema_kuznetsov.task_manager.models.Comment;
import com.tema_kuznetsov.task_manager.models.constrain.CommentConstrains;
import com.tema_kuznetsov.task_manager.repositories.CommentRepository;
import com.tema_kuznetsov.task_manager.repositories.TaskRepository;
import com.tema_kuznetsov.task_manager.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor // конструктор только для файнал полей
@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    //1. Создание комментария
    @Transactional
    public CommentResponseDto createComment(CommentCreateDto dto) {
        Comment comment = new Comment();
        comment.setText(dto.getText());

        // Извлекаем текущего пользователя из Authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        AppUser owner = userRepository.findUserByEmail(username)
                .orElseThrow(() -> new UserEmailNotFoundException(username));  // Получаем пользователя по email

        comment.setAuthor(owner);

        comment.setTask(taskRepository.findById(dto.getTask_id())
                .orElseThrow(() -> new TaskIdNotFoundException(dto.getTask_id())));

        // Сохранение комментария
        comment = commentRepository.save(comment);

        // Возвращаем DTO
        return new CommentResponseDto(comment);
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
