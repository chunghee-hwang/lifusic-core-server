package com.chung.lifusic.core.service;

import com.chung.lifusic.core.dto.FileCreateResponseDto;
import com.chung.lifusic.core.entity.File;
import com.chung.lifusic.core.entity.Music;
import com.chung.lifusic.core.entity.User;
import com.chung.lifusic.core.exception.NotFoundException;
import com.chung.lifusic.core.repository.FileRepository;
import com.chung.lifusic.core.repository.MusicRepository;
import com.chung.lifusic.core.repository.UserRepository;
import com.chung.lifusic.core.dto.MusicDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

// 아티스트 전용 서비스
@Service
@RequiredArgsConstructor
public class AdminMusicService {
    private final MusicRepository musicRepository;
    private final UserRepository userRepository;
    private final FileRepository fileRepository;

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
            throw new NotFoundException("Music not found - musicId: "+musicId);
        }

        return MusicDto.builder()
                .musicFileId(music.getMusicFile().getId())
                .thumbnailFileId(music.getThumbnailImageFile() == null ? null : music.getThumbnailImageFile().getId())
                .build();
    }

    public void deleteMusic(Long musicId) {
        musicRepository.deleteById(musicId);
    }
}
