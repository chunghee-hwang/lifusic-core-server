package com.chung.lifusic.core.controller;

import com.chung.lifusic.core.service.CustomSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/customer")
public class TestController {
    private final CustomSocketHandler customSocketHandler;
    @GetMapping("/socket-test")
    public ResponseEntity<String> socketTest() {
        customSocketHandler.sendMessageToAll("Hello!!");
        return ResponseEntity.ok("OK!");
    }
}
