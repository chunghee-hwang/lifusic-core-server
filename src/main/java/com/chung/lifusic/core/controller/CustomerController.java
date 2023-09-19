package com.chung.lifusic.core.controller;

import com.chung.lifusic.core.common.annotations.AuthorizationValid;
import com.chung.lifusic.core.common.enums.Role;
import com.chung.lifusic.core.dto.GetMusicsRequestDto;
import com.chung.lifusic.core.dto.SearchMusicResponseDto;
import com.chung.lifusic.core.service.CustomerMusicService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/music")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerMusicService customerMusicService;

    @GetMapping("/list")
    @AuthorizationValid(role= Role.CUSTOMER)
    public ResponseEntity<SearchMusicResponseDto> searchMusics(
        GetMusicsRequestDto request
    ) {
        return ResponseEntity.ok(customerMusicService.searchMusics(request));
    }
}
