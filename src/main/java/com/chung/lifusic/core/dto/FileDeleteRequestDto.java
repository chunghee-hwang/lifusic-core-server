package com.chung.lifusic.core.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class FileDeleteRequestDto {
    @NotNull
    private List<Long> fileIds; // 삭제될 DB에 저장된 파일 아이디
}
