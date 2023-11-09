package com.chung.lifusic.musicservice.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeleteMusicsInPlaylistRequestDto {
    @NotEmpty
    private List<Long> musicInPlaylistIds;
}
