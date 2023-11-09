package com.chung.lifusic.musicservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddMusicToPlaylistRequestDto {
    @NotNull
    private Long musicId;

    @NotNull
    private Long playlistId;
}
