package com.epita.social.service;

import com.epita.social.model.Story;
import com.epita.social.payload.DTO.ProfileStoriesDto;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface StoryService {
    public List<Story> getStories() throws Exception;
    public Story getStory(UUID id) throws Exception;
    public void addStory(Story story, MultipartFile file, String type) throws Exception;
    public void updateStory(UUID story_id, Story story, OAuth2AuthenticationToken user) throws Exception;
    public void deleteStory(UUID id, UUID profile_id, OAuth2AuthenticationToken user) throws Exception;
//    public Set<Set<Story>> getStoriesOfFollowersAndFollowing(UUID profile_id) throws Exception;
    public List<Story> getStoriesOfProfile(UUID profile_id) throws Exception;
    public Set<ProfileStoriesDto> getStoriesOfFollowersAndFollowing(UUID profileId) throws Exception;
}
