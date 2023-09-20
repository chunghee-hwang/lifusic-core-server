package com.chung.lifusic.core.repository;

import com.chung.lifusic.core.entity.Music;
import com.chung.lifusic.core.entity.MusicInPlaylist;
import com.chung.lifusic.core.entity.Playlist;
import com.chung.lifusic.core.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@SpringBootTest
@ActiveProfiles("local")
public class RepositoryTest {

    @Autowired
    private MusicRepository musicRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MusicInPlaylistRepository musicInPlaylistRepository;

    @Autowired
    private PlaylistRepository playlistRepository;

    @Test
    public void fetchMusicsByArtist() {
        Pageable pageable = PageRequest.of(1, 5, Sort.by(Sort.Order.asc("artistName")));
        User user = userRepository.findByEmail("test@email.com").orElseThrow();
        Page<Music> musics = musicRepository.findMusics(user.getId(), pageable);
    }

    @Test
    public void searchMusicsByKeyword() {
        final String keyword ="봄 비";
        Pageable pageable = PageRequest.of(1, 5, Sort.by(Sort.Order.asc("name")));
        Page<Music> musics = musicRepository.searchMusics(keyword, pageable);
    }

    @Test
    public void findMusicsInPlaylist() {
        List<MusicInPlaylist> mps = musicInPlaylistRepository.findMusicsInPlaylist(1L);
    }

    @Test
    public void findPlaylists() {
        User user = userRepository.findByEmail("test@email.com").orElseThrow();
        List<Playlist> playlists = playlistRepository.getPlaylistsByOwnerId(user.getId());
    }
}
