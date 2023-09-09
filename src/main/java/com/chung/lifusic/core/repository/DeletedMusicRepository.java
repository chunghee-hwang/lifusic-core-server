package com.chung.lifusic.core.repository;

import com.chung.lifusic.core.entity.DeletedMusic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeletedMusicRepository extends JpaRepository<DeletedMusic, Long> {
}
