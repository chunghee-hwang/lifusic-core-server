package com.chung.lifusic.musicservice.service;

import com.chung.lifusic.musicservice.common.utils.DateUtil;
import com.chung.lifusic.musicservice.dto.*;
import com.chung.lifusic.musicservice.entity.Music;
import com.chung.lifusic.musicservice.entity.MusicInPlaylist;
import com.chung.lifusic.musicservice.entity.Playlist;
import com.chung.lifusic.musicservice.entity.User;
import com.chung.lifusic.musicservice.exception.ForbiddenException;
import com.chung.lifusic.musicservice.exception.NotFoundException;
import com.chung.lifusic.musicservice.repository.MusicInPlaylistRepository;
import com.chung.lifusic.musicservice.repository.MusicRepository;
import com.chung.lifusic.musicservice.repository.PlaylistRepository;
import com.chung.lifusic.musicservice.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    /**
     * 음악 하나 찾기
     * @param musicId 찾으려는 음악 아이디
     */
    public GetMusicResponseDto getMusic(Long musicId) {
        Music music;
        try {
            music = musicRepository.findMusic(musicId).orElseThrow();
        } catch (NoSuchElementException exception) {
            throw new NotFoundException();
        }

        return GetMusicResponseDto.builder()
                .musicId(music.getId())
                .musicName(music.getName())
                .artistName(music.getArtistName())
                .fileId(music.getMusicFile().getId())
                .thumbnailImageUrl(music.getThumbnailImageUrl())
                .build();
    }

    /*
     * 고객이 음악 검색
     */
    public SearchMusicResponseDto searchMusics(SearchRequestDto request) {
        Pageable page = request.toPage("name", "artistName");
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
                .musicFileId(music.getMusicFile().getId())
                .thumbnailImageUrl(music.getThumbnailImageUrl())
                .build()).toList();
        return SearchMusicResponseDto.builder()
                .musics(musics)
                .page(musicsPage.getNumber() + 1)
                .allMusicSize(musicsPage.getTotalElements())
                .build();
    }

    /**
     * 플레이리스트 생성
     */
    public CreatePlaylistResponseDto createPlaylist(Long userId, CreatePlaylistRequestDto request) {
        User user;
        try {
            user = userRepository.findById(userId).orElseThrow();
        } catch (NoSuchElementException exception) {
            throw new ForbiddenException();
        }

        Playlist playlist = playlistRepository.save(Playlist.builder()
                .owner(user)
                .name(request.getName())
                .build());

        return CreatePlaylistResponseDto.builder()
                .playlistId(playlist.getId())
                .name(playlist.getName())
                .createdAt(playlist.getCreatedDate())
                .updatedAt(playlist.getUpdatedDate())
                .build();
    }

    /**
     * 고객의 모든 플레이리스트 가져오기
     */
    @Transactional()
    public List<PlaylistDto> getAllPlaylist(Long userId) {
        List<Playlist> playlists = playlistRepository.getPlaylistsByOwnerId(userId);
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

        Music music;
        Playlist playlist;

        try {
            music = musicRepository.findById(musicId).orElseThrow();
            playlist = playlistRepository.findById(playlistId).orElseThrow();
        } catch (NoSuchElementException exception) {
            throw new NotFoundException();
        }

        // 이미 재생 목록에 있는 음악인 지 확인. 있으면 추가 안 함
        boolean alreadyMusicExists = musicInPlaylistRepository.existsByPlaylistIdAndMusicId(playlist.getId(), music.getId());
        if (!alreadyMusicExists) {
            // 재생 목록에 음악 추가
            MusicInPlaylist musicInPlaylist = MusicInPlaylist.builder()
                    .music(music)
                    .playlist(playlist)
                    .build();

            musicInPlaylistRepository.save(musicInPlaylist);
        }

        return CommonResponseDto.builder()
                .success(true)
                .build();
    }

    /**
     * 플레이리스트에 들은 모든 음악 목록 가져오기
     */
    public List<MusicInPlaylistDto> getAllMusicInPlaylist(Long playlistId, SortRequestDto sortRequest) {
        Sort.Order order = sortRequest.toOrder("name", "artistName");
        String orderProperty = "m." + order.getProperty();
        Sort sort;
        if (order.isAscending()) {
            sort = Sort.by(orderProperty).ascending();
        } else {
            sort = Sort.by(orderProperty).descending();
        }
        List<MusicInPlaylist> musicsInPlaylist = musicInPlaylistRepository.findMusicsInPlaylist(playlistId, sort);
        return musicsInPlaylist.stream().map(musicInPlaylist -> {
            Music music = musicInPlaylist.getMusic();
            return MusicInPlaylistDto.builder()
                    .musicInPlaylistId(musicInPlaylist.getId())
                    .musicId(music.getId())
                    .musicName(music.getName())
                    .artistName(music.getArtistName())
                    .fileId(music.getMusicFile().getId())
                    .thumbnailImageUrl(music.getThumbnailImageUrl())
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
