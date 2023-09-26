package com.chung.lifusic.core.entity;

import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "music", indexes = {
        // 음악 이름 또는 아티스트 이름으로 검색 시 성능 개선을 위해 인덱스 추가
        @Index(name = "idxName", columnList = "name ASC"),
        @Index(name="idxArtistName", columnList = "artistName ASC")
})
public class Music extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private String artistName;

    @ManyToOne(fetch=FetchType.LAZY)
    private User artist;

    @OneToOne(fetch=FetchType.LAZY)
    private File musicFile;

    @OneToOne(fetch=FetchType.LAZY)
    private File thumbnailImageFile;

    public String getThumbnailImageUrl() {
        if (this.thumbnailImageFile == null) {
            return null;
        }
        Long fileId = this.thumbnailImageFile.getId();
        if (fileId == null) {
            return null;
        }
        return "/api/file/" + fileId;
    }
}
