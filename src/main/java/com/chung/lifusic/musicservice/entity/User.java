package com.chung.lifusic.musicservice.entity;

import com.chung.lifusic.musicservice.common.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="user")
public class User extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true)
    private String email;

    @Column
    private String name;

    @Column
    private String password;

    @Column
    @Enumerated(EnumType.STRING)
    private Role role;
}
