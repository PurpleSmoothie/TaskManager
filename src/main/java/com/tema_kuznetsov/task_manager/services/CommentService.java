package com.tema_kuznetsov.task_manager.services;

import com.tema_kuznetsov.task_manager.dto.comment.CommentCreateDto;
import com.tema_kuznetsov.task_manager.dto.comment.CommentResponseDto;
import com.tema_kuznetsov.task_manager.dto.comment.CommentUpdateDto;
import com.tema_kuznetsov.task_manager.exceptions.commentException.CommentIdNotFoundException;
import com.tema_kuznetsov.task_manager.exceptions.taskException.TaskIdNotFoundException;
import com.tema_kuznetsov.task_manager.exceptions.userException.emailException.UserEmailNotFoundException;
import com.tema_kuznetsov.task_manager.models.AppUser;
import com.tema_kuznetsov.task_manager.models.Comment;
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

/**
 * Сервис для работы с комментариями.
 * Предоставляет методы для создания, обновления, удаления и поиска комментариев.
 */
@AllArgsConstructor
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    /**
     * Создает новый комментарий.
     * Включает в себя установку автора комментария (из аутентификации пользователя) и привязку к задаче.
     *
     * @param dto объект, содержащий информацию о новом комментарии
     * @return DTO с информацией о созданном комментарии
     */
    @Transactional
    public CommentResponseDto createComment(CommentCreateDto dto) {
        Comment comment = new Comment();
        comment.setText(dto.getText());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        AppUser owner = userRepository.findUserByEmail(username)
                .orElseThrow(() -> new UserEmailNotFoundException(username));

        comment.setAuthor(owner);

        comment.setTask(taskRepository.findById(dto.getTask_id())
                .orElseThrow(() -> new TaskIdNotFoundException(dto.getTask_id())));

        comment = commentRepository.save(comment);

        return new CommentResponseDto(comment);
    }

    /**
     * Находит комментарий по его идентификатору.
     *
     * @param id идентификатор комментария
     * @return DTO с информацией о комментарии
     * @throws CommentIdNotFoundException если комментарий не найден
     */
    public CommentResponseDto findCommentById(Long id) {
        Comment comment = getCommentByIdOrThrow(id);
        return new CommentResponseDto(comment);
    }

    /**
     * Удаляет комментарий по его идентификатору.
     *
     * @param id идентификатор комментария
     * @throws CommentIdNotFoundException если комментарий не найден
     */
    @Transactional
    public void deleteCommentById(Long id) {
        Comment comment = getCommentByIdOrThrow(id);
        commentRepository.delete(comment);
    }

    /**
     * Обновляет комментарий по его идентификатору.
     * Обновляются только текстовые данные комментария.
     *
     * @param id  идентификатор комментария
     * @param dto объект с обновленным текстом комментария
     * @return DTO с информацией об обновленном комментарии
     * @throws CommentIdNotFoundException если комментарий не найден
     */
    @Transactional
    public CommentResponseDto updateCommentById(Long id, CommentUpdateDto dto) {
        Comment comment = getCommentByIdOrThrow(id);
        comment.setText(dto.getText());
        comment = commentRepository.save(comment);
        return new CommentResponseDto(comment);
    }

    /**
     * Получает комментарии для указанной задачи.
     *
     * @param taskId  идентификатор задачи
     * @param pageable объект для пагинации
     * @return список комментариев в виде страниц
     */
    public Page<CommentResponseDto> getCommentsForTask(Long taskId, Pageable pageable) {
        return commentRepository.findByTaskIdOrderByCreatedAtDesc(taskId, pageable)
                .map(CommentResponseDto::new);
    }

    /**
     * Получает комментарии для указанного пользователя.
     *
     * @param userId  идентификатор пользователя
     * @param pageable объект для пагинации
     * @return список комментариев в виде страниц
     */
    public Page<CommentResponseDto> getCommentsForUser(Long userId, Pageable pageable) {
        return commentRepository.findByAuthorIdOrderByCreatedAtDesc(userId, pageable)
                .map(CommentResponseDto::new);
    }

    /**
     * Находит комментарий по его идентификатору.
     * Если комментарий не найден, выбрасывает исключение {@link CommentIdNotFoundException}.
     *
     * @param id идентификатор комментария
     * @return найденный комментарий
     * @throws CommentIdNotFoundException если комментарий не найден
     */
    public Comment getCommentByIdOrThrow(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new CommentIdNotFoundException(id));
    }
}