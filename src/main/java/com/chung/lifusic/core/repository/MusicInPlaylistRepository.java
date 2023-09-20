package com.chung.lifusic.core.repository;

import com.chung.lifusic.core.entity.MusicInPlaylist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MusicInPlaylistRepository extends JpaRepository<MusicInPlaylist, Long> {

    // 플레이리스트에 있는 음악 목록 가져오기
    @Query(value = "select mp " +
            "from MusicInPlaylist mp " +
            "left join fetch mp.music m " +
            "left join fetch m.musicFile mf " +
            "left join fetch m.thumbnailImageFile mt " +
            "where mp.playlist.id = :playlistId")
    List<MusicInPlaylist> findMusicsInPlaylist(Long playlistId);
    // 플레이리스트에 음악이 있는 지 확인
    boolean existsByMusicId(Long musicId);
}
