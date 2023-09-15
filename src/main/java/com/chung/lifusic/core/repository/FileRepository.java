package com.chung.lifusic.core.repository;

import com.chung.lifusic.core.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File, Long> {
}