package com.laba5.gameportal.service;

import com.laba5.gameportal.dto.UserCreateDTO;
import com.laba5.gameportal.dto.UserLoginDTO;
import com.laba5.gameportal.entity.Game;
import com.laba5.gameportal.entity.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {

    ResponseEntity<?> registerUser(UserCreateDTO userCreateDTO);

    ResponseEntity<?> loginUser(UserLoginDTO userLoginDTO, HttpSession session);

    ResponseEntity<?> checkUserLogin(HttpSession session);

    ResponseEntity<?> getDashBoard(HttpSession session);

    ResponseEntity<?> userLogout(HttpSession session);

    ResponseEntity<?> updateUser(
            HttpSession session,
            Long id,
            String username,
            String password,
            String email,
            MultipartFile avatar,
            String role,
            String deleteavatar
    );

    ResponseEntity<?> getInfo(Long id, HttpSession session);

    void addToFavorites(Long id, Long id1);

    void removeFromFavorites(Long id, Long gameId);

    List<Game> getFavorites(Long id);

    User getUserById(Long id);

    User addFavoriteGame(Long id, Long gameId);

    User removeFavoriteGame(Long id, Long gameId);
}