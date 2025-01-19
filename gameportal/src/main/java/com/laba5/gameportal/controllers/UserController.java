package com.laba5.gameportal.controllers;

import com.laba5.gameportal.converters.Converter;
import com.laba5.gameportal.dto.*;
import com.laba5.gameportal.entity.User;
import com.laba5.gameportal.exception.FileUploadException;
import com.laba5.gameportal.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

    private final UserService userService;

    private final Converter converter;

    private boolean isCurrent(HttpSession session, Long id){
        UserResponseDTO userResponseDTO = (UserResponseDTO) session.getAttribute("user");
        if(userResponseDTO == null){
            return false;
        }
        return userResponseDTO.getId().equals(id);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String email,
            @RequestParam(value="avatar", required=false) MultipartFile avatar
    ){
        UserCreateDTO userCreateDTO = new UserCreateDTO(username,password,email,avatar);
        return userService.registerUser(userCreateDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginDTO userLoginDTO, HttpSession session){
        return userService.loginUser(userLoginDTO, session);
    }

    @GetMapping("/checklogin")
    public ResponseEntity<?> checkLogin(HttpSession session){
        return userService.checkUserLogin(session);
    }

    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashBoard(HttpSession session){
        return userService.getDashBoard(session);
    }

    @GetMapping("/logout")
    public ResponseEntity<?> userLogout(HttpSession session){
        return userService.userLogout(session);
    }

    @PutMapping("/updateUser/{id}")
    public ResponseEntity<?> updateUser(
            HttpSession session,
            @PathVariable Long id,
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String email,
            @RequestParam(value="avatar", required=false) MultipartFile avatar,
            @RequestParam String role,
            @RequestParam String deleteAvatar
    ){
        return userService.updateUser(
                session,
                id,
                username,
                password,
                email,
                avatar,
                role,
                deleteAvatar
        );
    }

    @GetMapping("/getinfoUser/{id}")
    public ResponseEntity<?> getInfo(@PathVariable Long id, HttpSession session){
        if(!isCurrent(session, id)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("У Вас недостаточно прав");
        }
        return userService.getInfo(id, session);
    }

    @GetMapping("/{id}/favorites")
    public ResponseEntity<?> getFavoriteGames(HttpSession session, @PathVariable Long id) {
        if(!isCurrent(session, id)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("У Вас недостаточно прав");
        }
        User user = userService.getUserById(id);
        if(user != null){
            List<GameDTO> favoriteGames = user.getFavorites().stream()
                    .map(converter::convertGameToDTO)
                    .collect(Collectors.toList());
            return new ResponseEntity<>(favoriteGames, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/{id}/favorites")
    public ResponseEntity<?> addFavoriteGame(
            HttpSession session,
            @PathVariable Long id,
            @RequestBody GameAddDTO gameId
    ) {
        if(!isCurrent(session, id)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("У Вас недостаточно прав");
        }
        return new ResponseEntity<>(converter.convertUserToDTO(userService.addFavoriteGame(id,gameId.getGameId())), HttpStatus.OK);
    }

    @DeleteMapping("/{id}/favorites/{gameId}")
    public ResponseEntity<?> removeFavoriteGame(
            HttpSession session,
            @PathVariable Long id,
            @PathVariable Long gameId
    ) {
        if(!isCurrent(session, id)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("У Вас недостаточно прав");
        }
        return new ResponseEntity<>(converter.convertUserToDTO(userService.removeFavoriteGame(id,gameId)), HttpStatus.OK);
    }

    @ExceptionHandler(FileUploadException.class)
    public ResponseEntity<String> handleFileUploadException(FileUploadException ex){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<String> handlemMaxUploadSizeExceededException (
            MaxUploadSizeExceededException ex
    ){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
