package com.chung.lifusic.core.controller;

import com.chung.lifusic.core.common.annotations.AuthenticatedUser;
import com.chung.lifusic.core.common.annotations.AuthorizationValid;
import com.chung.lifusic.core.common.enums.Role;
import com.chung.lifusic.core.service.FileStorageService;
import com.chung.lifusic.core.service.KafkaProducerService;
import dto.CommonResponseDto;
import dto.FileCreateRequestDto;
import dto.StoreTempFileResponseDto;
import dto.UserDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final FileStorageService fileStorageService;
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
        StoreTempFileResponseDto musicStoreResponse = fileStorageService.storeFileToTempDirWithRandomName(musicFile);
        FileCreateRequestDto.File musicTempFile = FileCreateRequestDto.File.builder()
                .tempFilePath(musicStoreResponse.getTempFilePath())
                .contentType(musicStoreResponse.getContentType())
                .originalFileName(musicStoreResponse.getOriginalFileName())
                .size(musicStoreResponse.getFileSize())
                .build();
        FileCreateRequestDto.File thumbnailTempFile = null;
        if (thumbnailImageFile != null) {
            StoreTempFileResponseDto musicThumbnailStoreResponse = fileStorageService.storeFileToTempDirWithRandomName(thumbnailImageFile);
            thumbnailTempFile = FileCreateRequestDto.File.builder()
                    .tempFilePath(musicThumbnailStoreResponse.getTempFilePath())
                    .contentType(musicThumbnailStoreResponse.getContentType())
                    .originalFileName(musicThumbnailStoreResponse.getOriginalFileName())
                    .size(musicThumbnailStoreResponse.getFileSize())
                .build();
        }

        // 카프카에게 파일 서버가 처리하도록 던짐
        kafkaProducerService.produceCreateFileRequest(FileCreateRequestDto.builder()
                        .requestUserId(authUser.getId())
                        .musicName(musicName)
                        .musicTempFile(musicTempFile)
                        .thumbnailTempFile(thumbnailTempFile)
                .build());

        return ResponseEntity.ok(CommonResponseDto.builder().success(true).build());
    }
}
