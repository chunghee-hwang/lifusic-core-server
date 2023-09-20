package com.chung.lifusic.core.controller;

import com.chung.lifusic.core.common.annotations.AuthenticatedUser;
import com.chung.lifusic.core.common.annotations.AuthorizationValid;
import com.chung.lifusic.core.common.enums.Role;
import com.chung.lifusic.core.dto.*;
import com.chung.lifusic.core.service.CustomerMusicService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/music")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerMusicService customerMusicService;

    /**
     * 음악 검색
     */
    @GetMapping("/list")
    @AuthorizationValid(role= Role.CUSTOMER)
    public ResponseEntity<SearchMusicResponseDto> searchMusics(
        SearchRequestDto request
    ) {
        return ResponseEntity.ok(customerMusicService.searchMusics(request));
    }

    /**
     * 재생 목록 만들기
     */
    @PostMapping("/playlist")
    @AuthorizationValid(role = Role.CUSTOMER)
    public ResponseEntity<CreatePlaylistResponseDto> createPlaylist(
            @AuthenticatedUser UserDto authUser,
            @RequestBody CreatePlaylistRequestDto request
    ) {
        return ResponseEntity.ok(customerMusicService.createPlaylist(authUser.getId(), request));
    }

    /**
     * “재생 목록”들 목록 가져오기
     */
    @GetMapping("/playlist/all")
    @AuthorizationValid(role=Role.CUSTOMER)
    public ResponseEntity<List<PlaylistDto>> getAllPlaylist(
            @AuthenticatedUser UserDto authUser
    ) {
        return ResponseEntity.ok(customerMusicService.getAllPlaylist(authUser.getId()));
    }

    /**
     * 재생 목록에 음악 추가. 이미 있는 노래면 추가 안 함
     */
    @PutMapping("/playlist/one")
    @AuthorizationValid(role=Role.CUSTOMER)
    public ResponseEntity<CommonResponseDto> addMusicToPlaylist(
            @Valid @RequestBody AddMusicToPlaylistRequestDto request
    ) {
        return ResponseEntity.ok(customerMusicService.addMusicToPlaylist(request));
    }

    /**
     * 재생 목록에 있는 음악 목록 가져오기
     */
    @GetMapping("/playlist/{playlistId}")
    @AuthorizationValid(role=Role.CUSTOMER)
    public ResponseEntity<List<MusicInPlaylistDto>> getMusicsInPlaylist(
            @PathVariable Long playlistId
    ) {
        return ResponseEntity.ok(customerMusicService.getAllMusicInPlaylist(playlistId));
    }

    @PostMapping("/playlist/batchDeleteMusics")
    @AuthorizationValid(role=Role.CUSTOMER)
    public ResponseEntity<CommonResponseDto> deleteMusicsInPlaylist(
            @Valid @RequestBody DeleteMusicsInPlaylistRequestDto request
    ) {
        return ResponseEntity.ok(customerMusicService.deleteMusicsInPlaylist(request));
    }
}
