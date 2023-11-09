package com.chung.lifusic.musicservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreatePlaylistResponseDto {
    private Long playlistId;
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
