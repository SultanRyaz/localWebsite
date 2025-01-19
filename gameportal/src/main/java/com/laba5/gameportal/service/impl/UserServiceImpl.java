package com.laba5.gameportal.service.impl;

import com.laba5.gameportal.dto.UserCreateDTO;
import com.laba5.gameportal.dto.UserLoginDTO;
import com.laba5.gameportal.dto.UserResponseDTO;
import com.laba5.gameportal.entity.Game;
import com.laba5.gameportal.entity.User;
import com.laba5.gameportal.exception.FileUploadException;
import com.laba5.gameportal.mapper.UserMapper;
import com.laba5.gameportal.repository.GameRepository;
import com.laba5.gameportal.repository.UserRepository;
import com.laba5.gameportal.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private GameRepository gameRepository;

    @Override
    public ResponseEntity<?> registerUser(UserCreateDTO userCreateDTO) {
        Optional<String> error = checkIfUserExists(userCreateDTO);
        if (error.isPresent()) {
            return new ResponseEntity<>(error.get(), HttpStatus.CONFLICT);
        }
        error = validateUserInput(userCreateDTO);
        if (error.isPresent()) {
            return new ResponseEntity<>(error.get(), HttpStatus.BAD_REQUEST);
        }
        User savedUser = userRepository.save(UserMapper.userCreateDtoToUser(userCreateDTO));
        return ResponseEntity.ok(UserMapper.userToUserResponseDto(savedUser));
    }

    @Override
    public ResponseEntity<?> loginUser(UserLoginDTO userLoginDTO, HttpSession session) {
        Optional<User> userOptional = userRepository.findByUsername(userLoginDTO.getUsername());
        if(userOptional.isEmpty()){
            return new ResponseEntity<>
                    ("Пользователь с таким логином не зарегистрирован", HttpStatus.NOT_FOUND);
        }
        User user = userOptional.get();
        if (!user.getPassword().equals(userLoginDTO.getPassword())) {
            return new ResponseEntity<>("Неправильный пароль", HttpStatus.UNAUTHORIZED);
        }
        UserResponseDTO userResponseDTO = UserMapper.userToUserResponseDto(user);
        session.setAttribute("user", userResponseDTO);
        System.out.println(session.getAttribute("user"));
        return ResponseEntity.ok(userResponseDTO);
    }

    @Override
    public ResponseEntity<?> checkUserLogin(HttpSession session){
        UserResponseDTO userResponseDTO = (UserResponseDTO) session.getAttribute("user");
        if(userResponseDTO == null){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> getDashBoard(HttpSession session){
        UserResponseDTO userResponseDTO = (UserResponseDTO) session.getAttribute("user");
        if(userResponseDTO == null){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return ResponseEntity.ok(userResponseDTO);
    }

    @Override
    public ResponseEntity<?> userLogout(HttpSession session){
        if(session.getAttribute("user") == null){
            return new ResponseEntity<>("Вы не вошли в аккаунт", HttpStatus.BAD_REQUEST);
        }
        session.removeAttribute("user");
        return ResponseEntity.ok("Вы успешно вышли из аккаунта");
    }

    private Optional<String> checkIfUserExists(UserCreateDTO userCreateDTO) {
        if (userRepository.findByUsername(userCreateDTO.getUsername()).isPresent()) {
            return Optional.of("Пользователь с таким логином уже зарегистрирован");
        }
        if (userRepository.findByEmail(userCreateDTO.getEmail()).isPresent()) {
            return Optional.of("Пользователь с такой почтой уже зарегистрирован");
        }
        return Optional.empty();
    }

    private Optional<String> validateUserInput(UserCreateDTO userCreateDTO) {
        if (!userCreateDTO.getUsername().matches
                ("^[a-zA-Z0-9]+$")) {
            return Optional.of("Неверное имя пользователя. " +
                    "Используйте только латинские буквы и цифры");
        }
        if (!userCreateDTO.getEmail().matches
                ("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
            return Optional.of("Неверный адрес электронной почты");
        }
        if (!userCreateDTO.getPassword().matches
                ("^[a-zA-Z0-9!@#$%^&*()_\\-+=\\[\\]{}|;:'\",.<>?/]{5,}$")) {
            return Optional.of("Пароль должен содержать минимум 5 символов: букв латинского" +
                    " алфавита, цифр или спецсимволов");
        }
        return Optional.empty();
    }

    @Override
    public ResponseEntity<?> updateUser(
            HttpSession session,
            Long id,
            String username,
            String password,
            String email,
            MultipartFile avatar,
            String role,
            String deleteavatar
    ){
        Optional<User> userOptional = userRepository.findById(id);
        User user = userOptional.get();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setRole(role);
        if(deleteavatar.equals("true")){
            UserMapper.deleteAvatar(user.getAvatar());
            user.setAvatar(UserMapper.getDefaultImageUrl());
        }
        if(avatar != null){
            UserMapper.deleteAvatar(user.getAvatar());
            user.setAvatar(UserMapper.processAvatar(avatar, user.getUsername()));
        }
        userRepository.save(user);
        return ResponseEntity.ok("Данные изменены");
    }

    public ResponseEntity<?> getInfo(Long id, HttpSession session){
        Optional<User> userOptional = userRepository.findById(id);
        if(userOptional.isPresent()){
            return ResponseEntity.ok(userOptional.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Пользователь не найден");
    }

    public List<Game> getFavorites(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with id " + userId + " not found"));;
        return user.getFavorites();
    }

    public void addToFavorites(Long userId, Long gameId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with id " + userId + " not found"));;
        Game game = gameRepository.findById(gameId).orElseThrow(() -> new NoSuchElementException("Game not found with ID " + gameId));
        if (user.getFavorites().contains(game)) return;
        user.getFavorites().add(game);
        userRepository.save(user);
    }

    public void removeFromFavorites(Long userId, Long gameId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with id " + userId + " not found"));;
        user.getFavorites().removeIf(game -> game.getId().equals(gameId));
        userRepository.save(user);
    }

    public User getUserById(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        return optionalUser.orElse(null);
    }

    public User addFavoriteGame(Long id, Long gameId) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User with id " + id + " not found"));;
        Game game = gameRepository.findById(gameId).orElseThrow(() -> new NoSuchElementException("Game not found with ID " + gameId));
        user.getFavorites().add(game);
        return userRepository.save(user);
    }

    public User removeFavoriteGame(Long id, Long gameId) {
        Optional<User> optionalUser = userRepository.findById(id);
        Optional<Game> optionalGame = gameRepository.findById(gameId);
        if (optionalUser.isPresent() && optionalGame.isPresent()) {
            User user = optionalUser.get();
            Game game = optionalGame.get();
            user.getFavorites().remove(game);
            return userRepository.save(user);
        }
        return null;
    }
}