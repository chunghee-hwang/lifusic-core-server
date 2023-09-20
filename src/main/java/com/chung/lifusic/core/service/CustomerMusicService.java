package com.chung.lifusic.core.service;

import com.chung.lifusic.core.common.utils.DateUtil;
import com.chung.lifusic.core.common.utils.PageUtil;
import com.chung.lifusic.core.dto.*;
import com.chung.lifusic.core.entity.Music;
import com.chung.lifusic.core.entity.MusicInPlaylist;
import com.chung.lifusic.core.entity.Playlist;
import com.chung.lifusic.core.entity.User;
import com.chung.lifusic.core.exception.ForbiddenException;
import com.chung.lifusic.core.repository.MusicInPlaylistRepository;
import com.chung.lifusic.core.repository.MusicRepository;
import com.chung.lifusic.core.repository.PlaylistRepository;
import com.chung.lifusic.core.repository.UserRepository;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.NoSuchElementException;


@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerMusicService {
    private final MusicRepository musicRepository;
    private final PlaylistRepository playlistRepository;
    private final UserRepository userRepository;
    private final MusicInPlaylistRepository musicInPlaylistRepository;
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

    /**
     * 플레이리스트 생성
     */
    public CreatePlaylistResponseDto createPlaylist(UserDto userDto, @Nullable String playlistName) {
        User user;
        try {
            user = userRepository.findById(userDto.getId()).orElseThrow();
        } catch (NoSuchElementException exception) {
            throw new ForbiddenException();
        }

        Playlist playlist = playlistRepository.save(Playlist.builder()
                .owner(user)
                .name(playlistName)
                .build());

        return CreatePlaylistResponseDto.builder()
                .playlistId(playlist.getId())
                .name(playlist.getName())
                .build();
    }

    /**
     * 고객의 모든 플레이리스트 가져오기
     */
    public List<PlaylistDto> getAllPlaylistResponse(UserDto userDto) {
        List<Playlist> playlists = playlistRepository.getPlaylistsByOwnerId(userDto.getId());
        return playlists.stream().map(playlist -> PlaylistDto.builder()
                .id(playlist.getId())
                .name(playlist.getName())
                .createdAt(DateUtil.localDateTimeToMilliseconds(playlist.getCreatedDate()))
                .build()).toList();
    }

    /**
     * 재생 목록에 음악 추가. 이미 있는 노래면 추가 안함
     */
    public CommonResponseDto addMusicToPlaylist(AddMusicToPlaylistRequestDto request) {
        Long musicId = request.getMusicId();
        Long playlistId = request.getPlaylistId();

        Music music = musicRepository.findById(musicId).orElseThrow();
        Playlist playlist = playlistRepository.findById(playlistId).orElseThrow();

        MusicInPlaylist musicInPlaylist = MusicInPlaylist.builder()
                .music(music)
                .playlist(playlist)
                .build();

        musicInPlaylistRepository.save(musicInPlaylist);
        return CommonResponseDto.builder()
                .success(true)
                .build();
    }

    /**
     * 플레이리스트에 들은 모든 음악 목록 가져오기
     */
    public List<MusicInPlaylistDto> getAllMusicInPlaylist(Long playlistId) {
        List<MusicInPlaylist> musicsInPlaylist = musicInPlaylistRepository.findMusicsInPlaylist(playlistId);
        return musicsInPlaylist.stream().map(musicInPlaylist -> {
            Music music = musicInPlaylist.getMusic();
            return MusicInPlaylistDto.builder()
                    .musicInPlaylistId(musicInPlaylist.getId())
                    .musicId(music.getId())
                    .musicName(music.getName())
                    .artistName(music.getArtistName())
                    .thumbnailImageUrl(music.getThumbnailImageUrl(GATEWAY_HOST))
                    .build();
        }).toList();
    }

    /**
     * 재생 목록에서 음악 삭제
     */
    public CommonResponseDto deleteMusicsInPlaylist(DeleteMusicsInPlaylistRequestDto request) {
        musicInPlaylistRepository.deleteAllByIdInBatch(
                request.getMusicInPlaylistIds()
        );
        return CommonResponseDto.builder().success(true).build();
    }
}
