package com.epita.social.controller;

import com.epita.social.model.Profile;
import com.epita.social.model.Story;
import com.epita.social.model.User;
import com.epita.social.payload.DTO.ProfileStoriesDto;
import com.epita.social.repo.ProfileRepo;
import com.epita.social.service.ProfileService;
import com.epita.social.service.StoryService;
import com.epita.social.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class StoryController {

    private final StoryService storyService;
    private final UserService userService;
    private final ProfileService profileService;
    private final ProfileRepo profileRepo;

    @ModelAttribute
    public void addAttributes(Model model, OAuth2AuthenticationToken user) throws Exception {
        var userDetails = user.getPrincipal().getAttributes();
        String email = userDetails.get("email").toString();
        User user_details = userService.findByEmail(email);
        Profile profile = profileRepo.findByUser(user_details);

        model.addAttribute("user", user_details);
        model.addAttribute("profile", profile);

    }

    @PostMapping(value = "/story/add/type={media_type}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addStory(@RequestPart("media") MultipartFile media,
                                      @RequestPart("story") Story story,
                                      @PathVariable("media_type") String media_type,
                                      OAuth2AuthenticationToken user) throws Exception {

        var userDetails = user.getPrincipal().getAttributes();
        String email = userDetails.get("email").toString();
        User user_details = userService.findByEmail(email);
        Profile profile = profileRepo.findByUser(user_details);
        story.setProfileId(profile.getProfile_id());
        story.setAuthor(profile.getUsername());
        storyService.addStory(story, media, media_type);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/me/{profileId}")
    public ResponseEntity<List<Story>> getStory(OAuth2AuthenticationToken user, @PathVariable("profileId") String profileId ) throws Exception {
//        var userDetails = user.getPrincipal().getAttributes();
//        String email = userDetails.get("email").toString();
//        User user_details = userService.findByEmail(email);
//        Profile profile = profileRepo.findByUser(user_details);
        return new ResponseEntity<>(storyService.getStoriesOfProfile(UUID.fromString(profileId)),HttpStatus.OK);
    }

    @GetMapping("/list")
    public ResponseEntity<List<Story>> getAllStorys() throws Exception {

        return new ResponseEntity<>(storyService.getStories(), HttpStatus.OK);
    }

    @GetMapping("/stories")
    public ResponseEntity<Set<ProfileStoriesDto>> getAllStoriesToUser(OAuth2AuthenticationToken user) throws Exception {
        var userDetails = user.getPrincipal().getAttributes();
        String email = userDetails.get("email").toString();
        User user_details = userService.findByEmail(email);
        Profile profile = profileRepo.findByUser(user_details);
        Set<ProfileStoriesDto> stories = storyService.getStoriesOfFollowersAndFollowing(profile.getProfile_id());

        return new ResponseEntity<>(stories, HttpStatus.OK);
    }

    @PatchMapping("/update")
    public ResponseEntity<?> updateStory(@RequestParam("story_id") UUID story_id,
                                         @RequestBody Story story, OAuth2AuthenticationToken user) throws Exception {
        storyService.updateStory(story_id, story, user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteStory(@RequestParam("story_id") UUID story_id,
                                         OAuth2AuthenticationToken user) throws Exception {
        UUID profile_id = null;
        storyService.deleteStory(story_id, profile_id, user);
        return new ResponseEntity<>(HttpStatus.OK);
    }


}
