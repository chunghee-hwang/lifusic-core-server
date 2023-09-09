package com.chung.lifusic.core.repository;

import com.chung.lifusic.core.entity.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PlaylistRepository extends JpaRepository<Playlist, Long> {

    // 플레이리스트 주인 아이디로 플레이리스트 목록 가져오기
    // (현재는 1개만 default로 저장하고 있으나, 추후 플레이리스트를 여러 개 저장할 것을 대비)
    List<Playlist> getPlaylistsByOwnerId(Long ownerId);
}
