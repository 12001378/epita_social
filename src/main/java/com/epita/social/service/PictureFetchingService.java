package com.epita.social.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PictureFetchingService {
    private final OAuth2AuthorizedClientService clientService;

    public byte[] fetchUserPhoto(String accessToken, String photoUrl) {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setAccept(List.of(MediaType.IMAGE_JPEG));

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<byte[]> response = new RestTemplate().exchange(
                photoUrl, HttpMethod.GET, entity, byte[].class);

        return response.getBody();
    }

    public String getAccessToken(OAuth2AuthenticationToken authentication) {
        System.err.println(authentication.getAuthorizedClientRegistrationId() + "-----------" + authentication.getName());
        OAuth2AuthorizedClient client = clientService.loadAuthorizedClient(
                authentication.getAuthorizedClientRegistrationId(),
                authentication.getName()
        );
        return client.getAccessToken().getTokenValue();
    }
}
