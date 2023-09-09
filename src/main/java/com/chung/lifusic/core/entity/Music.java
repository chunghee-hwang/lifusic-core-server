package com.chung.lifusic.core.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
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

    @OneToOne
    private User artist;

    @OneToOne
    private File musicFile;

    @OneToOne
    private File thumbnailImageFile;
}
