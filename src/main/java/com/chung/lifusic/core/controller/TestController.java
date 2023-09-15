package com.chung.lifusic.core.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/customer")
public class TestController {
    @GetMapping("/socket-test")
//    @AuthorizationValid(role= Role.CUSTOMER)
    public ResponseEntity<String> socketTest() {
        return ResponseEntity.ok("OK!");
    }
}
