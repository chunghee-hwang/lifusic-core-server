package com.chung.lifusic.core.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchMusicResponseDto {
    private long allMusicSize;
    private int page;
    private List<Music> musics;

    @Builder
    @Getter
    public static class Music {
        private Long id;
        private String name;
        private String artistName;
        private String thumbnailImageUrl;
    }
}
