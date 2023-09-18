package com.chung.lifusic.core.service;

import com.chung.lifusic.core.dto.FileDto;
import com.chung.lifusic.core.dto.FileResponseDto;
import com.chung.lifusic.core.entity.File;
import com.chung.lifusic.core.entity.Music;
import com.chung.lifusic.core.entity.User;
import com.chung.lifusic.core.repository.FileRepository;
import com.chung.lifusic.core.repository.MusicRepository;
import com.chung.lifusic.core.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

// 아티스트 전용 서비스
@Service
@RequiredArgsConstructor
public class AdminMusicService {
    private final MusicRepository musicRepository;
    private final UserRepository userRepository;
    private final FileRepository fileRepository;

    @Transactional
    public void createMusic(FileResponseDto response) {
        Long userId = response.getRequestUserId();
        FileResponseDto.Content content = response.getContent();
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
}
