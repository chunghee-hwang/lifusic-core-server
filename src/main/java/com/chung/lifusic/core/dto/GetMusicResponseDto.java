package com.chung.lifusic.core.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetMusicResponseDto {
    private Long musicId;
    private Long fileId;
    private String musicName;
    private String artistName;
    private String thumbnailImageUrl;
}
