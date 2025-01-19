package com.laba5.gameportal.service.impl;

import com.laba5.gameportal.entity.News;
import com.laba5.gameportal.entity.User;
import com.laba5.gameportal.repository.NewsRepository;
import com.laba5.gameportal.repository.UserRepository;
import com.laba5.gameportal.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class NewsServiceImpl implements NewsService {

    private final NewsRepository newsRepository;
    private final UserRepository userRepository;

    @Override
    public List<News> getAllNews() {
        return newsRepository.findAll();
    }

    @Override
    public News getNewsById(Long id) {
        return newsRepository.findById(id).orElse(null);
    }

    @Override
    public ResponseEntity<News> addNews(News newsItem, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with id " + userId + " not found"));
        newsItem.setAuthor(user.getUsername());
        newsItem.setDate(LocalDateTime.now());
        return ResponseEntity.ok(newsRepository.save(newsItem));
    }

    @Override
    public News updateNews(Long id, News updatedNewsItem, Long userId) {
        News existingNewsItem = newsRepository.findById(id).orElse(null);
        if (existingNewsItem == null) {
            return null;
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with id " + userId + " not found"));
        existingNewsItem.setAuthor(user.getUsername());
        existingNewsItem.setTitle(updatedNewsItem.getTitle());
        existingNewsItem.setContent(updatedNewsItem.getContent());
        existingNewsItem.setImagePath(updatedNewsItem.getImagePath());
        return newsRepository.save(existingNewsItem);
    }

    @Override
    public boolean deleteNews(Long id) {
        if (!newsRepository.existsById(id)) {
            return false;
        }
        newsRepository.deleteById(id);
        return true;
    }
}