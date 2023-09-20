package com.chung.lifusic.core.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileDeleteRequestDto {
    @NotNull
    private Long requestUserId; // 요청한 유저 아이디

    @NotNull
    private Long musicFileId; // 삭제될 DB에 저장된 파일 아이디
    private Long thumbnailFileId;
}
