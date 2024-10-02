package com.meta.air_jet.user.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "tbl_user")
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String loginId;
    private String password;
    private String nickname;

    @Builder
    public User(String loginId, String password, String nickname) {
        this.loginId = loginId;
        this.password = password;
        this.nickname = nickname;
    }
}
