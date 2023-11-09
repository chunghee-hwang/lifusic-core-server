package com.chung.lifusic.musicservice.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeleteArtistMusicsRequestDto {
    @NotEmpty
    private List<Long> musicIds;
}
