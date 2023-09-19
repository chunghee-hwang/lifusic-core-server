package com.chung.lifusic.core.entity;

import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "music")
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
}
