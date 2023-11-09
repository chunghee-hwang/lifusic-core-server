package com.chung.lifusic.musicservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlaylistDto {
    private Long id;
    private String name;
    private Long createdAt;
}
