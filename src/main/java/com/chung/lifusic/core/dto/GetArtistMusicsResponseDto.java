package com.chung.lifusic.core.dto;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetArtistMusicsResponseDto {
    private Integer totalPage; // 전체 페이지
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
