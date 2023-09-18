package com.chung.lifusic.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MusicDto {
    private Long id;
    private Long musicFileId;
    private Long thumbnailFileId;
}
