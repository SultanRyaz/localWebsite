package com.laba5.gameportal.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class DateController {

    @GetMapping("/time")
    public ResponseEntity<String> time(){
        try{
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.uuuu HH:mm:ss");
            LocalDateTime ldt = LocalDateTime.now();
            return ResponseEntity.ok(dtf.format(ldt));
        }catch (Exception ex){
            return ResponseEntity.status(500).body("Не удалось отобразить дату/время");
        }
    }
}
