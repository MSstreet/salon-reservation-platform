package com.salon.core.domain.repository;

import com.salon.core.domain.entity.OAuthUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OAuthUserRepository extends JpaRepository<OAuthUser, Long> {

    Optional<OAuthUser> findByProviderAndProviderId(String provider, String providerId);
}