package com.chung.lifusic.core.repository;

import com.chung.lifusic.core.entity.MusicInPlaylist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MusicInPlaylistRepository extends JpaRepository<MusicInPlaylist, Long> {

    // 플레이리스트에 있는 음악 목록 가져오기
    @Query(value = "select mp from MusicInPlaylist mp " +
            "left join fetch mp.music " +
            "where mp.playlist.id = :playlistId"
            , countQuery = "select count(mp.id) from MusicInPlaylist mp " +
            "where mp.playlist.id = :playlistId"
    )
    Page<MusicInPlaylist> findMusicsInPlaylist(Long playlistId, Pageable pageable);

}
