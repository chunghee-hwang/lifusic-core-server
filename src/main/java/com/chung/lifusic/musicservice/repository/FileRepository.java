package com.chung.lifusic.musicservice.repository;

import com.chung.lifusic.musicservice.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File, Long> {
}
