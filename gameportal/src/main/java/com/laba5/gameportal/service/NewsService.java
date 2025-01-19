package com.laba5.gameportal.service;

import com.laba5.gameportal.entity.News;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface NewsService {
    List<News> getAllNews();
    News getNewsById(Long id);
    ResponseEntity<News> addNews(News newsItem, Long userId);
    News updateNews(Long id, News updatedNewsItem, Long userId);
    boolean deleteNews(Long id);
}


