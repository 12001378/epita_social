package com.epita.social.service;

import com.epita.social.exception.GlobleException;
import com.epita.social.model.Profile;
import com.epita.social.model.Story;
import com.epita.social.model.User;
import com.epita.social.repo.ProfileRepo;
import com.epita.social.repo.StroyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoryServiceImpl implements StoryService{

    private final StroyRepository storyRepository;
    private final GlobleException globleException;
    private final UserService userService;
    private final ProfileService profileService;
    private final ProfileRepo profileRepo;
    private final CloudinaryService cloudinaryService;


    @Override
    public List<Story> getStories() throws Exception {
        return storyRepository.findAll();
    }

    @Override
    public Story getStory(UUID id) throws Exception {
        return storyRepository.findById(id).orElseThrow(() -> new Exception("Story not found"));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addStory(Story story, MultipartFile file, String type) throws Exception {
        String media_url = cloudinaryService.upload(file, type);
        Story newStory = new Story();
        newStory.setMedia(media_url);
        newStory.setCaption(story.getCaption());
        newStory.setProfileId(story.getProfileId());
        storyRepository.save(story);
    }

    @Override
    public void updateStory(UUID story_id,Story story, OAuth2AuthenticationToken user) throws Exception {

        Story existing_story = getStory(story_id);
        existing_story.setCaption(story.getCaption());
        existing_story.setMedia(story.getMedia());
        var userDetails = user.getPrincipal().getAttributes();
        String email = userDetails.get("email").toString();
        User user_details = userService.findByEmail(email);
        Profile profile = profileRepo.findByUser(user_details);
        if(user_details != null && story.getProfileId().equals(profile.getProfile_id())) {
            storyRepository.save(existing_story);
        }else{
            throw new Exception("You are not allowed to update this story");
        }

    }

    @Override
    public void deleteStory(UUID id, UUID profile_id, OAuth2AuthenticationToken user) throws Exception {
        Story story = getStory(id);
        var userDetails = user.getPrincipal().getAttributes();
        String email = userDetails.get("email").toString();
        User user_details = userService.findByEmail(email);
        Profile profile = profileRepo.findByUser(user_details);
        profile_id = profile.getProfile_id();

        if(user_details != null && story.getProfileId().equals(profile_id)) {
            storyRepository.delete(story);
        }else{
            throw new Exception("You are not allowed to delete this story");
        }
    }

    @Override
//    @Cache(usage = "")
    public Set<List<Story>> getStoriesOfFollowersAndFollowing(UUID profile_id) throws Exception {

        Profile profile = profileService.getProfile(profile_id);
        Set<User> following = profile.getFollowing();
        Set<User> followers = profile.getFollowers();
         following.addAll(followers);

         List<Story> stories = new ArrayList<>();
        Set<List<Story>> followingProfileIds = following.stream()
                .map(user -> {
                    try {
                        return profileRepo.findByUser(user).getStory();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toSet());

        return followingProfileIds;
    }
}
