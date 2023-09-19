package com.chung.lifusic.core.service;

import com.chung.lifusic.core.dto.FileCreateResponseDto;
import com.chung.lifusic.core.dto.GetMusicsRequestDto;
import com.chung.lifusic.core.dto.GetArtistMusicsResponseDto;
import com.chung.lifusic.core.dto.MusicDto;
import com.chung.lifusic.core.entity.File;
import com.chung.lifusic.core.entity.Music;
import com.chung.lifusic.core.entity.User;
import com.chung.lifusic.core.exception.ForbiddenException;
import com.chung.lifusic.core.exception.NotFoundException;
import com.chung.lifusic.core.repository.FileRepository;
import com.chung.lifusic.core.repository.MusicRepository;
import com.chung.lifusic.core.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

// 아티스트 전용 서비스
@Service
@RequiredArgsConstructor
public class AdminMusicService {
    private final MusicRepository musicRepository;
    private final UserRepository userRepository;
    private final FileRepository fileRepository;
    private final RestTemplate restTemplate;

    @Value("${host.server.file}")
    private String fileServerHost;

    @Value("${host.server.gateway}")
    private String gatewayHost;

    @Transactional
    public void createMusic(FileCreateResponseDto response) {
        Long userId = response.getRequestUserId();
        FileCreateResponseDto.Content content = response.getContent();
        User user = userRepository.findById(userId).orElseThrow();
        File musicFile = fileRepository.findById(content.getMusicFileId()).orElseThrow();

        Long thumbnailFileId = content.getThumbnailFileId();
        File thumbnailFile = null;
        if (thumbnailFileId != null) {
            thumbnailFile = fileRepository.findById(thumbnailFileId).orElseGet(() -> null);
        }
        Music music = Music.builder()
                .musicFile(musicFile)
                .thumbnailImageFile(thumbnailFile)
                .name(content.getMusicName())
                .artist(user)
                .artistName(user.getName())
                .build();
        musicRepository.save(music);
    }

    public MusicDto getMusic(Long musicId) {
        Music music = null;
        try {
            music = musicRepository.findById(musicId).orElseThrow();
        } catch (NoSuchElementException exception) {
            throw new NotFoundException("Music not found - musicId: " + musicId);
        }

        return MusicDto.builder()
                .musicFileId(music.getMusicFile().getId())
                .artistId(music.getArtist().getId())
                .thumbnailFileId(music.getThumbnailImageFile() == null ? null : music.getThumbnailImageFile().getId())
                .build();
    }

    public void deleteMusic(Long musicId) {
        musicRepository.deleteById(musicId);
    }

    public void downloadMusicFile(Long authUserId, Long musicId, HttpServletResponse response) {
        Music music = null;
        try {
            music = musicRepository.findById(musicId).orElseThrow();
        } catch (NoSuchElementException exception) {
            throw new NotFoundException("Music not found - musicId: " + musicId);
        }
        if (!music.getArtist().getId().equals(authUserId)) {
            throw new ForbiddenException();
        }

        File musicFile = music.getMusicFile();
        // Optional Accept header
        RequestCallback requestCallback = request -> request.getHeaders()
                .setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM, MediaType.ALL));

        // Streams the response instead of loading it all in memory
        ResponseExtractor<Void> responseExtractor = res -> {
            // Here I write the response to a file but do what you like
            InputStream io = res.getBody();

            final String contentDisposition = "attachment; filename=\"" + musicFile.getOriginalFileName() + "\"";
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, contentDisposition);
            io.transferTo(response.getOutputStream());
            return null;
        };
        // file 서버에서 파일을 가져옴
        restTemplate.execute(URI.create(this.fileServerHost + "/api/file/" + musicFile.getId()), HttpMethod.GET, requestCallback, responseExtractor);
    }

    public GetArtistMusicsResponseDto getMusicsByArtistId(Long artistId, GetMusicsRequestDto request) {
        Pageable page = request.getPage("updatedDate");
        String keyword = request.getKeyword();
        Page<Music> musicsPage;
        if (keyword == null) {
            musicsPage = musicRepository.findMusics(artistId, page);
        } else {
            musicsPage = musicRepository.findMusics(artistId, keyword, page);
        }
        List<GetArtistMusicsResponseDto.Music> musics= musicsPage.getContent().stream().map(music-> {
            Long thumbnailImageFileId = music.getThumbnailImageFile().getId();
            String thumbnailImageUrl = gatewayHost + "/api/file/" + thumbnailImageFileId;
            return GetArtistMusicsResponseDto.Music
                    .builder()
                    .id(music.getId())
                    .name(music.getName())
                    .thumbnailImageUrl(thumbnailImageUrl)
                    .build();
        }).toList();
        return GetArtistMusicsResponseDto.builder()
                .musics(musics)
                .page(musicsPage.getNumber() + 1)
                .totalPage(musicsPage.getTotalPages())
                .build();
    }
}
