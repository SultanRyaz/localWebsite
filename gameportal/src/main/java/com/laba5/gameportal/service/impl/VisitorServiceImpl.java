package com.laba5.gameportal.service.impl;

import com.laba5.gameportal.entity.Visitor;
import com.laba5.gameportal.repository.VisitorRepository;
import com.laba5.gameportal.service.VisitorService;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class VisitorServiceImpl implements VisitorService {

    private VisitorRepository visitorRepository;

    @Override
    public ResponseEntity<?> incrementVisitorCount(HttpSession session){
        Optional<Visitor> visitorOptional = visitorRepository.findById(1L);
        Visitor visitor;
        if(visitorOptional.isPresent()){
            visitor = visitorOptional.get();
        }else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        String visitedStatus = (String) session.getAttribute("visited");
        System.out.println("Visited status: " + visitedStatus);
        if(visitedStatus == null){
            Long count = visitor.getCount() + 1;
            visitor.setCount(count);
            visitorRepository.save(visitor);
            session.setAttribute("visited", "true");
        }
        return ResponseEntity.ok(visitor.getCount());
    }

}