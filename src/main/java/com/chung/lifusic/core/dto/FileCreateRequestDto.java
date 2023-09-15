package com.chung.lifusic.core.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileCreateRequestDto {
    private Long requestUserId; // 요청한 유저 아이디
    private String musicName; // 음악 제목

    private FileDto musicTempFile;
    private FileDto thumbnailTempFile;
}
