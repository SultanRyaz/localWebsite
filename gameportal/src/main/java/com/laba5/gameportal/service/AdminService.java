package com.laba5.gameportal.service;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface AdminService {

    ResponseEntity<?> getUsers(HttpSession session);

    ResponseEntity<?> deleteUser(HttpSession session, Long id);

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

    ResponseEntity<?> addUser(
            HttpSession session,
            String username,
            String password,
            String email,
            String role,
            MultipartFile avatar
    );

    ResponseEntity<?> getInfo(Long id, HttpSession session);
}