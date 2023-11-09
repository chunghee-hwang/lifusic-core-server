package com.chung.lifusic.musicservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileCreateRequestDto {
    @NotNull
    private Long requestUserId; // 요청한 유저 아이디
    @NotBlank
    private String musicName; // 음악 제목

    @NotNull
    private FileDto musicTempFile;
    private FileDto thumbnailTempFile;
}
