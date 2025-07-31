package com.epita.social.service;

import com.epita.social.model.Post;
import com.epita.social.model.Profile;
import com.epita.social.model.User;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface ProfileService {

    public List<Profile> getAllProfiles() throws Exception;
    public Profile getProfile(UUID profile_id) throws Exception;
    public Profile addProfile(Profile profile) throws Exception;
    public Profile updateProfile(Profile profile, UUID profile_id) throws Exception;
    public Set<User> followingUsers(UUID profile_id) throws Exception;
    public Set<User> followersUsers(UUID profile_id) throws Exception;
    public Set<Post> savedPosts(UUID profile_id) throws Exception;
    public Set<Post> taggedPosts(UUID profile_id) throws Exception;
    public void addFollower(UUID profile_id, UUID follower_id) throws Exception;
    public void removeFollower(UUID profile_id, UUID follower_id) throws Exception;
    public void update_profile_pic(UUID profile_id, Profile p) throws Exception;
    public void update_cover_pic(UUID profile_id, Profile p) throws Exception;
    public Set<Profile> getProfilesByUsername(String username) throws Exception;
    public Set<Profile> getSuggestions(UUID user_id) throws Exception;
    public boolean followButton(UUID profile_id, OAuth2AuthenticationToken user) throws Exception;

}
