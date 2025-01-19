package com.laba5.gameportal.controllers;

import com.laba5.gameportal.dto.UserResponseDTO;
import com.laba5.gameportal.entity.News;
import com.laba5.gameportal.service.NewsService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class NewsController {

    private final NewsService newsService;

    private boolean isModer(HttpSession session){
        UserResponseDTO userResponseDTO = (UserResponseDTO) session.getAttribute("user");
        if(userResponseDTO == null){
            return false;
        }
        return userResponseDTO.getRole().equals("MODER");
    }

    @Value("${file.upload.dir}")
    private String uploadDir;

    @GetMapping
    public List<News> getAllNews() {
        return newsService.getAllNews();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getNewsById(HttpSession session, @PathVariable Long id) {
        if(!isModer(session)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("У Вас недостаточно прав");
        }
        News newsItem = newsService.getNewsById(id);
        if (newsItem == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(newsItem, HttpStatus.OK);
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<?> addNews(
            HttpSession session,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam("userId") Long userId
    ) throws IOException {
        if(!isModer(session)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("У Вас недостаточно прав");
        }
        String imagePath = null;
        if (image != null && !image.isEmpty()) {
            imagePath = saveImage(image);
        }
        News newsItem = new News();
        newsItem.setTitle(title);
        newsItem.setContent(content);
        newsItem.setImagePath(imagePath);
        return newsService.addNews(newsItem, userId);
    }

    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public ResponseEntity<?> updateNews(
            HttpSession session,
            @PathVariable Long id,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam(value = "deleteImage", required = false) boolean deleteImage,
            @RequestParam("userId") Long userId
    ) throws IOException {
        if(!isModer(session)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("У Вас недостаточно прав");
        }
        News existingNews = newsService.getNewsById(id);
        if(existingNews == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        String imagePath = existingNews.getImagePath();
        if(deleteImage){
            deleteImageFile(imagePath);
            imagePath = null;
        }

        if (image != null && !image.isEmpty()) {
            if(imagePath != null)
            {
                deleteImageFile(imagePath);
            }

            imagePath = saveImage(image);
        }
        News updatedNewsItem = new News();
        updatedNewsItem.setTitle(title);
        updatedNewsItem.setContent(content);
        updatedNewsItem.setImagePath(imagePath);

        News updatedNews = newsService.updateNews(id, updatedNewsItem, userId);
        if (updatedNews == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(updatedNews, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNews(HttpSession session, @PathVariable Long id) {
        if(!isModer(session)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("У Вас недостаточно прав");
        }
        boolean deleted = newsService.deleteNews(id);
        if (!deleted) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private String saveImage(MultipartFile image) throws IOException {
        String fileName = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(image.getInputStream(), filePath);
        return fileName;
    }
    private void deleteImageFile(String imagePath){
        if(imagePath != null && !imagePath.isEmpty()) {
            Path filePath = Paths.get(uploadDir).resolve(imagePath);
            try {
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                System.err.println("Failed to delete image: " + filePath.toString() + " " + e.getMessage());
            }
        }
    }
}