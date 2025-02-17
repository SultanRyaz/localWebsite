package com.laba5.gameportal.repository;

import com.laba5.gameportal.entity.Game;
import com.laba5.gameportal.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    List<User> findAllByFavoritesContains(Game game);
}