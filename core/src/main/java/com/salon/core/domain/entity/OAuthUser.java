package com.salon.core.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "oauth_user",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_oauth_provider", columnNames = {"provider", "provider_id"})
    })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OAuthUser extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String provider;

    @Column(name = "provider_id", nullable = false, length = 255)
    private String providerId;

    @Column(nullable = false, length = 255)
    private String email;

    @Column(length = 100)
    private String name;

    private OAuthUser(String provider, String providerId, String email, String name) {
        this.provider = provider;
        this.providerId = providerId;
        this.email = email;
        this.name = name;
    }

    public static OAuthUser create(String provider, String providerId, String email, String name) {
        return new OAuthUser(provider, providerId, email, name);
    }

    public void updateProfile(String email, String name) {
        this.email = email;
        this.name = name;
    }
}