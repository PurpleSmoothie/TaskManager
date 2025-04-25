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

@Repository
public interface UserRepository extends JpaRepository<AppUser, Long> {

    boolean existsById(Long id);
    boolean existsByLogin(String login);
    boolean existsByEmail(String email);
    Optional<AppUser> findUserByLogin(String title);
    Page<AppUser> findUserByLoginContaining(String titlePart, Pageable pageable);

    Page<AppUser> findUsersByRole(String status, Pageable pageable);
    Optional<AppUser> findUserByEmail(String email);

    @Transactional
    @Modifying
    @Query("DELETE FROM AppUser a WHERE a.login = :login")
    void deleteUserByLogin(@Param("login") String login);
}
