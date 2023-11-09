package com.chung.lifusic.musicservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MusicInPlaylistDto {
    private Long musicInPlaylistId;
    private Long musicId;
    private Long fileId;
    private String musicName;
    private String artistName;
    private String thumbnailImageUrl;
}
