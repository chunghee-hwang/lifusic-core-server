package com.chung.lifusic.musicservice.dto;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetArtistMusicsResponseDto {
    private Long allMusicSize; // 전체 음악 개수
    private Integer page; // 현재 페이지
    private List<Music> musics;

    @Getter
    @Builder
    public static class Music {
        private Long id; // 음악 아이디
        private String name; // 음악 이름
        private String thumbnailImageUrl;
    }
}
