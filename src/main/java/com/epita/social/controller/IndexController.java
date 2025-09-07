package com.epita.social.controller;

import com.epita.social.model.Post;
import com.epita.social.model.Profile;
import com.epita.social.model.Story;
import com.epita.social.model.User;
import com.epita.social.payload.DTO.ProfileStoriesDto;
import com.epita.social.repo.ProfileRepo;
import com.epita.social.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;
import java.util.Set;

@Controller
@RequiredArgsConstructor
public class IndexController {

    private final UserService userService;
    private final PictureFetchingService graphService;
    private final ProfileService profileService;
    private final ProfileRepo profileRepo;
    private final PostService postService;
    private final StoryService storyService;

    @GetMapping("/api/v1/home")
    @Transactional(rollbackFor = Exception.class)
    public String index(Model model, OAuth2AuthenticationToken user) throws Exception {
        var userDetails = user.getPrincipal().getAttributes();
        String email = userDetails.get("email").toString();

        String firstName = userDetails.get("given_name").toString();
        String lastName = userDetails.get("family_name").toString();
        String photo = userDetails.get("picture").toString();
        User user1 = new User();
        Profile profile = new Profile();
        if (!userService.checkUserExiststance(email)) {
            user1.setEmail(email);
            user1.setFirstName(firstName);
            user1.setLastName(lastName);
            User saved_user = userService.save(user1);

            profile.setUser(user1);
            profile.setUsername(saved_user.getFirstName() + "_" + saved_user.getLastName());
            profileService.addProfile(profile);
        } else {
            User existing_user = userService.findByEmail(email);
            Set<Profile> suggested_profiles = profileService.getSuggestions(existing_user.getUserId());
            model.addAttribute("user", existing_user);
            model.addAttribute("profiles", suggested_profiles);
            User existing_user1 = userService.findByEmail(email);
            Profile profile1 = profileRepo.findByUser(existing_user1);
            model.addAttribute("profile", profile);
//            List<Post> feed = postService.getFeed(user);


            List<Post> feed = postService.getAllPosts();
            // Set likedByCurrentUser and savedByCurrentUser for each post
            Set<Post> savedPosts = profile1.getSavedPosts();
            for (Post post : feed) {
                post.setLikedByCurrentUser(
                    post.getLiked().stream().anyMatch(u -> u.getEmail().equals(email))
                );
                post.setSavedByCurrentUser(savedPosts != null && savedPosts.contains(post));
            }
            model.addAttribute("feed", feed);

            Set<ProfileStoriesDto> storyList = storyService.getStoriesOfFollowersAndFollowing(profile1.getProfile_id());
            model.addAttribute("storyList", storyList);

        }

        return "index";
    }

    @GetMapping("/api/v1/main/user")
    public ResponseEntity<?> user(Model model, OAuth2AuthenticationToken user) throws Exception {
        var userDetails = user.getPrincipal().getAttributes();
        String email = userDetails.get("email").toString();
        User user_details = userService.findByEmail(email);
        model.addAttribute("user", user_details);
        return new ResponseEntity<>(user_details, HttpStatus.OK);
    }

    @GetMapping("/user/photo")
    public ResponseEntity<byte[]> getUserPhoto(OAuth2AuthenticationToken authentication) {
        var userDetails = authentication.getPrincipal().getAttributes();
        String photo = userDetails.get("picture").toString();
        String accessToken = graphService.getAccessToken(authentication);
        byte[] photoBytes = graphService.fetchUserPhoto(accessToken, photo);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(photoBytes);
    }

    @GetMapping("/")
    public String home() {
        return "home";
    }

}
