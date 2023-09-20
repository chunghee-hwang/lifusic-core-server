package com.chung.lifusic.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePlaylistRequestDto {
    // option
    private String name; // 플레이리스트 이름
}
