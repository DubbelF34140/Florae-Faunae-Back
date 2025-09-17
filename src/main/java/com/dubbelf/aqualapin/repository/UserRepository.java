package com.dubbelf.aqualapin.repository;

import com.dubbelf.aqualapin.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findById(UUID id);

    @Query("SELECT u FROM User u WHERE u.id = :id")
    User findByIdObject(@Param("id") UUID id);

    User findByPseudo(String pseudo);

    User findByEmail(String email);

    Boolean existsByPseudo(String pseudo);

    Boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.pseudo = :pseudo")
    User findBypseudo(@Param("pseudo") String pseudo);
}