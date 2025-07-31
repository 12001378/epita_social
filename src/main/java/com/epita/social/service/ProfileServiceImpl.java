package com.epita.social.service;

import com.epita.social.model.Post;
import com.epita.social.model.Profile;
import com.epita.social.model.User;
import com.epita.social.repo.PostRepo;
import com.epita.social.repo.ProfileRepo;
import com.epita.social.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepo profileRepo;
    private final UserRepo userRepo;
    private final PostRepo postRepo;
    private final UserService userService;


    @Override
    public List<Profile> getAllProfiles() throws Exception {
        return profileRepo.findAll();
    }

    @Override
    public Profile getProfile(UUID profile_id) throws Exception {
        return profileRepo.findById(profile_id).orElseThrow();
    }

    @Override
    public Profile addProfile(Profile profile) throws Exception {
        return profileRepo.save(profile);
    }

    @Override
    public Profile updateProfile(Profile profile, UUID profile_id) throws Exception {
        Profile existingProfile = getProfile(profile_id);
        existingProfile.setProfile_bio(profile.getProfile_bio());
        existingProfile.setProfile_background(profile.getProfile_background());
        existingProfile.setProfile_picture(profile.getProfile_picture());
        existingProfile.setAccountType(profile.isAccountType());
        return profileRepo.save(existingProfile);
    }

    @Override
    public Set<User> followingUsers(UUID profile_id) throws Exception {
        Profile profile = getProfile(profile_id);
        return profile.getFollowing();
    }

    @Override
    public Set<User> followersUsers(UUID profile_id) throws Exception {
        Profile profile = getProfile(profile_id);
        return profile.getFollowers();
    }

    @Override
    public Set<Post> savedPosts(UUID profile_id) throws Exception {
        Profile profile = getProfile(profile_id);
        return profile.getSavedPosts();
    }

    @Override
    public Set<Post> taggedPosts(UUID profile_id) throws Exception {
        Profile profile = getProfile(profile_id);
        return profile.getTaggedPosts();
    }

    @Override
    public void addFollower(UUID profile_id, UUID follower_id) throws Exception {
        Profile profileViewdToFolllow = getProfile(profile_id);
        User follower = userService.findById(follower_id);
        Profile profile = profileRepo.findByUser(follower);
        Set<User> followers = followersUsers(profile_id);
        Set<User> followingUsers = followingUsers(profile.getProfile_id());
        followingUsers.add(profileViewdToFolllow.getUser());
        followers.add(follower);

    }

    @Override
    public void removeFollower(UUID profile_id, UUID follower_id) throws Exception {
        Profile profileViewdToFolllow = getProfile(profile_id);
        User follower = userService.findById(follower_id);
        Profile profile = profileRepo.findByUser(follower);
        Set<User> followers = followersUsers(profile_id);
        Set<User> followingUsers = followingUsers(profile.getProfile_id());
        followingUsers.remove(profileViewdToFolllow.getUser());
        followers.remove(follower);
    }

    @Override
    public void update_profile_pic(UUID profile_id, Profile p) throws Exception {
        Profile existingProfile = getProfile(profile_id);
        existingProfile.setProfile_picture(p.getProfile_picture());
        profileRepo.save(existingProfile);
    }

    @Override
    public void update_cover_pic(UUID profile_id, Profile p) throws Exception {
        Profile existingProfile = getProfile(profile_id);
        existingProfile.setProfile_background(p.getProfile_background());
        profileRepo.save(existingProfile);
        System.err.println("updated");

    }

    @Override
    public Set<Profile> getProfilesByUsername(String username) throws Exception {
        Set<Profile> profiles = profileRepo.findByQuery(username);
        if (profiles.size() == 0) {
            throw new Exception("No user found");
        }
        return profiles;

    }

    @Override
    public Set<Profile> getSuggestions(UUID userId) throws Exception {

        List<Profile> allProfiles = getAllProfiles();

        User currentUser = userRepo.findById(userId)
                .orElseThrow(() -> new Exception("User not found"));
        Profile profiles = profileRepo.findByUser(currentUser);

        List<User> currentFriends = new ArrayList<>(profiles.getFollowers().stream().toList());
        List<User> currentFollowers = new ArrayList<>(profiles.getFollowing().stream().toList());
        currentFollowers.addAll(currentFriends);
        Set<Profile> suggestions = new HashSet<>();

        for (Profile profile : allProfiles) {
            User profileUser = profile.getUser();

            if (profileUser.getUserId().equals(userId)) {
                continue;
            }
            if (currentFollowers.contains(profileUser)) {
                continue;
            }
            suggestions.add(profile);
        }

        return suggestions;
    }

    @Override
    public boolean followButton(UUID profile_id, OAuth2AuthenticationToken token) throws Exception {
        var user_details = token.getPrincipal().getAttributes();
        String email = user_details.get("email").toString();
        User user = userService.findByEmail(email);
        Profile profile = profileRepo.findByUser(user);
        List<UUID> following = profile.getFollowing().stream().map(profile1 ->
        {
            User user1 = new User();
            Profile profile2 = new Profile();
           try {
               user1 = userService.findById(profile1.getUserId());
               profile2 = profileRepo.findByUser(user1);
               profile2.getProfile_id();
           }catch (Exception e) {
               e.printStackTrace();
           }
           return profile2.getProfile_id();
        }).collect(Collectors.toList());
        boolean isSame = profile.getProfile_id().equals(profile_id);
        boolean isFollower = following.contains(profile_id);
        if (isSame) {
            return false;
        }
        if (isFollower) {
            return false;
        }
        return true;
    }

}
