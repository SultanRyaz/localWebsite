package com.laba5.gameportal.controllers;

import com.laba5.gameportal.service.VisitorService;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class VisitorController {

    private final VisitorService visitorService;

    @GetMapping("/incrementvisitor")
    public ResponseEntity<?> incrementVisitorCount(HttpSession session){
        return visitorService.incrementVisitorCount(session);
    }
}
