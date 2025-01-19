package com.laba5.gameportal.service.impl;

import com.laba5.gameportal.dto.UserResponseDTO;
import com.laba5.gameportal.entity.User;
import com.laba5.gameportal.mapper.UserMapper;
import com.laba5.gameportal.repository.UserRepository;
import com.laba5.gameportal.service.AdminService;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
@AllArgsConstructor
public class AdminServiceImpl implements AdminService {

    private UserRepository userRepository;

    private static final Long MAIN_ADMIN_ID = 1L;

    private boolean isAdmin(HttpSession session){
        UserResponseDTO userResponseDTO = (UserResponseDTO) session.getAttribute("user");
        if(userResponseDTO == null){
            return false;
        }
        return userResponseDTO.getRole().equals("ADMIN");
    }

    @Override
    public ResponseEntity<?> getUsers(HttpSession session){
        if(!isAdmin(session)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("У Вас недостаточно прав");
        }
        return ResponseEntity.ok(UserMapper.usersListToUsersResponseDtoList(userRepository.findAll()));
    }

    @Override
    public ResponseEntity<?> deleteUser(HttpSession session, Long id){
        if(!isAdmin(session)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("У Вас недостаточно прав");
        }
        if(id.equals(MAIN_ADMIN_ID)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Невозможно удалить главного администратора");
        }
        UserResponseDTO userResponseDTO = (UserResponseDTO)session.getAttribute("user");
        if(userResponseDTO.getId().equals(id)){
            session.removeAttribute("user");
        }
        Optional<User> userOptional = userRepository.findById(id);
        User user;
        if(userOptional.isEmpty()){
            return ResponseEntity.status(404).body("Пользователь не найден");
        }
        user = userOptional.get();
        UserMapper.deleteAvatar(user.getAvatar());
        userRepository.deleteById(id);
        return ResponseEntity.ok("Пользователь успешно удален");
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
        if(!isAdmin(session)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("У Вас недостаточно прав");
        }
        Optional<User> userOptional = userRepository.findById(id);
        if(userOptional.isEmpty()){
            return new ResponseEntity<>
                    ("Пользователя с таким ID не существует", HttpStatus.NOT_FOUND);
        }
        User user = userOptional.get();
        Optional<String> checker = checkIfUserConflicts(user, username, email);
        if(checker.isPresent()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(checker.get());
        };
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

    public ResponseEntity<?> addUser(
            HttpSession session,
            String username,
            String password,
            String email,
            String role,
            MultipartFile avatar
    ){
        if(!isAdmin(session)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("У Вас недостаточно прав");
        }
        Optional<String> error = checkIfUserExists(username, email);
        if(error.isPresent()){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error.get());
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setRole(role);
        user.setAvatar(UserMapper.processAvatar(avatar, username));
        User savedUser = userRepository.save(user);
        return ResponseEntity.ok(savedUser);
    }

    public ResponseEntity<?> getInfo(Long id, HttpSession session){
        if(!isAdmin(session)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("У Вас недостаточно прав");
        }
        Optional<User> userOptional = userRepository.findById(id);
        if(userOptional.isPresent()){
            return ResponseEntity.ok(userOptional.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Пользователь не найден");
    }

    private Optional<String> checkIfUserExists(String username, String email) {
        if (userRepository.findByUsername(username).isPresent()) {
            return Optional.of("Пользователь с таким логином уже зарегистрирован");
        }
        if (userRepository.findByEmail(email).isPresent()) {
            return Optional.of("Пользователь с такой почтой уже зарегистрирован");
        }
        return Optional.empty();
    }

    private Optional<String> checkIfUserConflicts(User user, String username, String email) {
        if(!user.getUsername().equals(username)){
            Optional<User> userOptional = userRepository.findByUsername(username);
            if(userOptional.isPresent()){
                return Optional.of("Пользователь с таким именем уже существует");
            }
        }
        if(!user.getEmail().equals(email)){
            Optional<User> userOptional = userRepository.findByEmail(email);
            if(userOptional.isPresent()){
                return Optional.of("Пользователь с такой почтой уже существует");
            }
        }
        return Optional.empty();
    }
}