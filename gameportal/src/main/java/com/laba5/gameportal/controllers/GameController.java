package com.laba5.gameportal.controllers;

import com.laba5.gameportal.converters.Converter;
import com.laba5.gameportal.dto.GameDTO;
import com.laba5.gameportal.dto.UserResponseDTO;
import com.laba5.gameportal.entity.Game;
import com.laba5.gameportal.entity.User;
import com.laba5.gameportal.repository.GameRepository;
import com.laba5.gameportal.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/games")
@CrossOrigin(origins = "http://localhost:5173")
public class GameController {

    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Converter converter;

    @Value("${file.upload.dir}") // Получаем путь из application.properties
    private String uploadDir;

    private boolean isAdmin(HttpSession session){
        UserResponseDTO userResponseDTO = (UserResponseDTO) session.getAttribute("user");
        if(userResponseDTO == null){
            return false;
        }
        return userResponseDTO.getRole().equals("ADMIN");
    }

    private boolean isLogin(HttpSession session){
        UserResponseDTO userResponseDTO = (UserResponseDTO) session.getAttribute("user");
        if(userResponseDTO == null){
            return false;
        }
        return true;
    }

    @PostMapping
    public ResponseEntity<?> addGame(HttpSession session, @ModelAttribute GameData gameData, @RequestParam("image") MultipartFile image) {
        if(!isAdmin(session)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("У Вас недостаточно прав");
        }
        try {
            String fileName = storeFile(image);
            Game game = new Game();
            game.setDescription(gameData.getDescription());
            game.setName(gameData.getName());
            game.setImage(fileName);
            Game savedGame = gameRepository.save(game);
            return new ResponseEntity<>(savedGame, HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    private String storeFile(MultipartFile file) throws IOException{
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        String originalFileName = file.getOriginalFilename();
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String fileName = UUID.randomUUID().toString() + fileExtension;

        try(InputStream inputStream = file.getInputStream()){
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(inputStream,filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch(IOException e){
            throw new IOException("Ошибка при сохранении файла", e);
        }
        return fileName;
    }

    @GetMapping
    public ResponseEntity<?> getAllGames(HttpSession session) {
        if(!isLogin(session)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("У Вас недостаточно прав");
        }
        List<Game> games = gameRepository.findAll();
        List<GameDTO> favoriteGames = gameRepository.findAll().stream()
                .map(converter::convertGameToDTO)
                .collect(Collectors.toList());
        return new ResponseEntity<>(favoriteGames, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGame(HttpSession session, @PathVariable Long id) {
        if(!isAdmin(session)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("У Вас недостаточно прав");
        }
        Optional<Game> optionalGame = gameRepository.findById(id);
        if (optionalGame.isPresent()) {
            Game game = optionalGame.get();
            // Получаем всех пользователей, у которых игра в избранном
            List<User> usersWithFavorite = userRepository.findAllByFavoritesContains(game);

            // Убираем игру из избранного у этих пользователей
            for (User user : usersWithFavorite) {
                user.getFavorites().remove(game);
                userRepository.save(user); //Сохраняем user после удаления элемента
            }
            // Удаляем игру
            gameRepository.delete(game);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    // Вспомогательный класс для получения текстовых данных
    static class GameData {
        private String name;
        private String description;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

    }
}