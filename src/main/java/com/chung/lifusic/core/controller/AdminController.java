package com.chung.lifusic.core.controller;

import com.chung.lifusic.core.common.annotations.AuthenticatedUser;
import com.chung.lifusic.core.common.annotations.AuthorizationValid;
import com.chung.lifusic.core.common.enums.Role;
import com.chung.lifusic.core.dto.*;
import com.chung.lifusic.core.service.AdminMusicService;
import com.chung.lifusic.core.service.FileStorageService;
import com.chung.lifusic.core.service.KafkaProducerService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final FileStorageService fileStorageService;
    private final AdminMusicService adminMusicService;
    private final KafkaProducerService kafkaProducerService;
    @PostMapping("/music")
    @AuthorizationValid(role=Role.ADMIN)
    public ResponseEntity<CommonResponseDto> addMusic(
            @AuthenticatedUser() UserDto authUser,
            @RequestPart(value = "musicName") @NotBlank String musicName,
            @RequestPart(value = "musicFile") MultipartFile musicFile,
            @RequestPart(value = "thumbnailImageFile", required = false) MultipartFile thumbnailImageFile
    ) {
        // 파일 임시 저장
        FileDto musicTempFile = fileStorageService.storeFileToTempDirWithRandomName(musicFile);
        FileDto thumbnailTempFileDto = null;
        if (thumbnailImageFile != null) {
            thumbnailTempFileDto = fileStorageService.storeFileToTempDirWithRandomName(thumbnailImageFile);
        }

        // 카프카에게 파일 서버가 처리하도록 던짐
        kafkaProducerService.produceCreateFileRequest(FileCreateRequestDto.builder()
                        .requestUserId(authUser.getId())
                        .musicName(musicName)
                        .musicTempFile(musicTempFile)
                        .thumbnailTempFile(thumbnailTempFileDto)
                .build());

        return ResponseEntity.ok(CommonResponseDto.builder().success(true).build());
    }

    @DeleteMapping("/music/{musicId}")
    @AuthorizationValid(role=Role.ADMIN)
    public ResponseEntity<CommonResponseDto> deleteMusic(
            @AuthenticatedUser() UserDto authUser,
            @PathVariable Long musicId
    ) {
        MusicDto music = adminMusicService.getMusic(musicId);
        adminMusicService.deleteMusic(musicId);
        kafkaProducerService.produceDeleteFileRequest(FileDeleteRequestDto
                .builder()
                        .requestUserId(authUser.getId())
                        .musicFileId(music.getMusicFileId())
                        .thumbnailFileId(music.getThumbnailFileId())
                .build());
        return ResponseEntity.ok(CommonResponseDto.builder().success(true).build());
    }

    @GetMapping(path = "/music/{musicId}/file", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @AuthorizationValid(role=Role.ADMIN)
    public void downloadMusicFile(
            @AuthenticatedUser() UserDto authUser,
            @PathVariable Long musicId,
            HttpServletResponse response
    ) {
        this.adminMusicService.downloadMusicFile(authUser.getId(), musicId, response);
    }

    @GetMapping("/musics")
    @AuthorizationValid(role=Role.ADMIN)
    public ResponseEntity<GetArtistMusicsResponseDto> getMyMusics(
            @AuthenticatedUser() UserDto authUser,
            GetMusicsRequestDto request
    ) {
        return ResponseEntity.ok(adminMusicService.getMusicsByArtistId(authUser.getId(), request));
    }
}
