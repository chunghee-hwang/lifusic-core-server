package com.chung.lifusic.core.service;

import com.chung.lifusic.core.common.utils.PageUtil;
import com.chung.lifusic.core.dto.GetMusicsRequestDto;
import com.chung.lifusic.core.dto.SearchMusicResponseDto;
import com.chung.lifusic.core.entity.Music;
import com.chung.lifusic.core.repository.MusicRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerMusicService {
    private final MusicRepository musicRepository;

    @Value("${host.server.gateway}")
    private String GATEWAY_HOST;

    /*
     * 고객이 음악 검색
     */
    public SearchMusicResponseDto searchMusics(GetMusicsRequestDto request) {
        Pageable page = PageUtil.getPage(
                request.getPage(),
                request.getLimit(),
                request.getOrderBy(),
                request.getOrderDirection(),
                "name", "artistName"
        );
        Page<Music> musicsPage;
        String keyword = request.getKeyword();
        if (StringUtils.hasText(keyword)) {
            musicsPage = musicRepository.searchMusics(keyword, page);
        } else {
            musicsPage = musicRepository.searchMusics(page);
        }
        List<SearchMusicResponseDto.Music> musics = musicsPage.getContent().stream().map(music -> SearchMusicResponseDto.Music.builder()
                .id(music.getId())
                .name(music.getName())
                .artistName(music.getArtistName())
                .thumbnailImageUrl(music.getThumbnailImageUrl(GATEWAY_HOST))
                .build()).toList();
        return SearchMusicResponseDto.builder()
                .musics(musics)
                .page(musicsPage.getNumber() + 1)
                .totalPage(musicsPage.getTotalPages())
                .build();
    }
}
