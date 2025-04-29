package com.tema_kuznetsov.task_manager.repositories;

import com.tema_kuznetsov.task_manager.models.AppUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Репозиторий для работы с сущностью {@link AppUser}.
 * Предоставляет методы для работы с пользователями, включая их поиск и удаление.
 */
@Repository
public interface UserRepository extends JpaRepository<AppUser, Long> {

    /**
     * Проверяет, существует ли пользователь с данным идентификатором.
     *
     * @param id Идентификатор пользователя.
     * @return true, если пользователь существует.
     */
    boolean existsById(Long id);

    /**
     * Проверяет, существует ли пользователь с таким логином.
     *
     * @param login Логин пользователя.
     * @return true, если пользователь с таким логином существует.
     */
    boolean existsByLogin(String login);

    /**
     * Проверяет, существует ли пользователь с таким email.
     *
     * @param email Email пользователя.
     * @return true, если пользователь с таким email существует.
     */
    boolean existsByEmail(String email);

    /**
     * Находит пользователя по логину.
     *
     * @param title Логин пользователя.
     * @return Опциональный пользователь.
     */
    Optional<AppUser> findUserByLogin(String title);

    /**
     * Находит пользователей, логины которых содержат заданную часть строки.
     *
     * @param titlePart Часть логина.
     * @param pageable Параметры пагинации.
     * @return Страница с пользователями.
     */
    Page<AppUser> findUserByLoginContaining(String titlePart, Pageable pageable);

    /**
     * Находит пользователей по роли.
     *
     * @param status Роль пользователя.
     * @param pageable Параметры пагинации.
     * @return Страница с пользователями.
     */
    Page<AppUser> findUsersByRole(String status, Pageable pageable);

    /**
     * Находит пользователя по email.
     *
     * @param email Email пользователя.
     * @return Опциональный пользователь.
     */
    Optional<AppUser> findUserByEmail(String email);

    /**
     * Удаляет пользователя по логину.
     *
     * @param login Логин пользователя.
     */
    @Transactional
    @Modifying
    @Query("DELETE FROM AppUser a WHERE a.login = :login")
    void deleteUserByLogin(@Param("login") String login);
}