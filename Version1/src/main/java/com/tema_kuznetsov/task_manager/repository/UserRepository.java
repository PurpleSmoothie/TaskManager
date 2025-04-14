package com.tema_kuznetsov.task_manager.repository;

import com.tema_kuznetsov.task_manager.model.App_user;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<App_user, Long> {
    // Базовые методы (уже есть в JpaRepository):
    // save(), findById(), findAll(), deleteById(), count() и т.д.

    public boolean existsById(Long id);
    boolean existsByLogin(String login);
    boolean existsByEmail(String email);
    Optional<App_user> findUserByLogin(String title); // Важно: возвращаем Optional
    Page<App_user> findUserByLoginContaining(String titlePart, Pageable pageable); // По части имени

    // Фильтрация по роли и имейлу
    Page<App_user> findUsersByRole(String status,Pageable pageable);
    Optional<App_user> findUserByEmail(String email);

    @Transactional // Весь метод выполняется как одна транзакция
    @Modifying // Указывает, что запрос изменяет данные (не SELECT)
    // Требуется для INSERT, UPDATE, DELETE операций
    @Query("DELETE FROM App_user a WHERE a.login = :login")
    void deleteUserByLogin(@Param("login") String login);
}
