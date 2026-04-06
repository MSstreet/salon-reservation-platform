package com.salon.api.auth.oauth;

import com.salon.core.domain.entity.OAuthUser;
import com.salon.core.domain.repository.OAuthUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private static final String PROVIDER_GOOGLE = "google";
    private static final String ATTRIBUTE_SUB = "sub";
    private static final String ATTRIBUTE_EMAIL = "email";
    private static final String ATTRIBUTE_NAME = "name";
    private static final String ATTRIBUTE_OAUTH_USER_ID = "oauthUserId";

    private final OAuthUserRepository oAuthUserRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        Map<String, Object> attributes = oAuth2User.getAttributes();
        String providerId = (String) attributes.get(ATTRIBUTE_SUB);
        String email = (String) attributes.get(ATTRIBUTE_EMAIL);
        String name = (String) attributes.get(ATTRIBUTE_NAME);

        OAuthUser oAuthUser = oAuthUserRepository.findByProviderAndProviderId(PROVIDER_GOOGLE, providerId)
                .map(existing -> {
                    existing.updateProfile(email, name);
                    return oAuthUserRepository.save(existing);
                })
                .orElseGet(() -> oAuthUserRepository.save(OAuthUser.create(PROVIDER_GOOGLE, providerId, email, name)));

        Map<String, Object> enrichedAttributes = new HashMap<>(attributes);
        enrichedAttributes.put(ATTRIBUTE_OAUTH_USER_ID, oAuthUser.getId());

        return new DefaultOAuth2User(
                oAuth2User.getAuthorities(),
                enrichedAttributes,
                ATTRIBUTE_SUB
        );
    }
}
