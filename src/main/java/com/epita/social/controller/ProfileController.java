package com.epita.social.controller;

import com.epita.social.model.Profile;
import com.epita.social.model.User;
import com.epita.social.repo.ProfileRepo;
import com.epita.social.service.ProfileService;
import com.epita.social.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;
import java.util.UUID;

@Controller
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;
    private final UserService userService;
    private final ProfileRepo profileRepo;

    @ModelAttribute
    public void addAttributes(Model model, OAuth2AuthenticationToken user) throws Exception {
        var userDetails = user.getPrincipal().getAttributes();
        String email = userDetails.get("email").toString();
        User user_details = userService.findByEmail(email);
        model.addAttribute("user_details", user_details);
    }

    @GetMapping("/profile/{profile_id}")
    public String getProfile(@PathVariable("profile_id") UUID profile_id, Model model, OAuth2AuthenticationToken token) throws Exception {
        Profile profile =  profileService.getProfile(profile_id);
        model.addAttribute("profile", profile);
        boolean follow_btn = profileService.followButton(profile_id,token);
        model.addAttribute("follow_btn", follow_btn);
        return "profile";
    }

    @GetMapping("/getProfileDetails/{profile_id}")
    public ResponseEntity<Profile> getProfile(@PathVariable("profile_id") String profile_id, Model model, OAuth2AuthenticationToken token) throws Exception {
        UUID profileId = UUID.fromString(profile_id);
        Profile profile =  profileService.getProfile(profileId);
        model.addAttribute("profile", profile);
        boolean follow_btn = profileService.followButton(profileId,token);
        model.addAttribute("follow_btn", follow_btn);
        return new ResponseEntity<>(profile, HttpStatus.OK);
    }

    @GetMapping("/profile")
    public String getUserProfile(OAuth2AuthenticationToken user_auth, Model model) throws Exception {
        var userDetails = user_auth.getPrincipal().getAttributes();
        String email = userDetails.get("email").toString();
        User user1 = userService.findByEmail(email);

        Profile profile = profileRepo.findByUser(user1);
        model.addAttribute("user", user1);
        model.addAttribute("profile", profile);
        return "profile";
    }

    @PostMapping("/profile/follow")
    public ResponseEntity<?> addFollowers(@RequestParam("profile_id") String profile_id, OAuth2AuthenticationToken user_auth) throws Exception {
        UUID profileId = UUID.fromString(profile_id);
        var userDetails = user_auth.getPrincipal().getAttributes();
        String email = userDetails.get("email").toString();
        User user1 = userService.findByEmail(email);
        profileService.addFollower(profileId, user1.getUserId());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping("/profile/{profile_id}")
    public ResponseEntity<?> removeFollowers(@PathVariable("profile_id") UUID profile_id, OAuth2AuthenticationToken user_auth) throws Exception {
        var userDetails = user_auth.getPrincipal().getAttributes();
        String email = userDetails.get("email").toString();
        User user1 = userService.findByEmail(email);
        profileService.removeFollower(profile_id, user1.getUserId());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping(path = "/profile")
    public ResponseEntity<?> updateProfile(OAuth2AuthenticationToken user_auth,
                                           @RequestBody Profile profile) throws Exception {
        var userDetails = user_auth.getPrincipal().getAttributes();
        String email = userDetails.get("email").toString();
        User user1 = userService.findByEmail(email);
        User user = userService.findById(user1.getUserId());
        Profile profile1 = profileRepo.findByUser(user);
        profileService.updateProfile(profile, profile1.getProfile_id());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/upload")
    public ResponseEntity<?> update_dp(@RequestParam("profile_pic") MultipartFile profile_pic,
                                       OAuth2AuthenticationToken user_auth) throws Exception {
        var userDetails = user_auth.getPrincipal().getAttributes();
        String email = userDetails.get("email").toString();
        User user1 = userService.findByEmail(email);
        User user = userService.findById(user1.getUserId());
        Profile profile = profileRepo.findByUser(user);
        profile.setProfile_picture(profile_pic.getBytes());
        profileService.update_profile_pic(profile.getProfile_id(), profile);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/upload_cover")
    public ResponseEntity<?> update_cover(@RequestParam("cover_pic") MultipartFile cover_pic,
                                          OAuth2AuthenticationToken user_auth) throws Exception {
        var userDetails = user_auth.getPrincipal().getAttributes();
        String email = userDetails.get("email").toString();
        User user1 = userService.findByEmail(email);
        System.err.println(user1.toString());
        User user = userService.findById(user1.getUserId());
        Profile profile = profileRepo.findByUser(user);
        profile.setProfile_background(cover_pic.getBytes());
        profileService.update_cover_pic(profile.getProfile_id(), profile);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/profile/search")
    public ResponseEntity<Set<Profile>> searchProfile(@RequestParam("name") String username) throws Exception {
        Set<Profile> profiles = profileService.getProfilesByUsername(username);
        System.out.println(profiles);
        return ResponseEntity.ok(profiles);
    }

    @GetMapping("/suggestions")
    public ResponseEntity<Set<Profile>> suggestedProfiles(OAuth2AuthenticationToken user_auth) throws Exception {
        var userDetails = user_auth.getPrincipal().getAttributes();
        String email = userDetails.get("email").toString();
        User user1 = userService.findByEmail(email);
        Set<Profile> suggested_profiles = profileService.getSuggestions(user1.getUserId());
        return ResponseEntity.ok(suggested_profiles);
    }

    @GetMapping("/image")
    @ResponseBody
    public ResponseEntity<byte[]> getImage(OAuth2AuthenticationToken user_auth) throws Exception {
        var userDetails = user_auth.getPrincipal().getAttributes();
        String email = userDetails.get("email").toString();
        User user = userService.findByEmail(email);
        Profile profile = profileRepo.findByUser(user);
        byte[] imageData = profile.getProfile_picture();
        if (imageData == null || imageData.length == 0) {
            // Load default image from resources
            ClassPathResource defaultImage = new ClassPathResource("static/images/default-cover.jpeg");
            imageData = defaultImage.getInputStream().readAllBytes();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(imageData);
    }

    @GetMapping("/image/{profile_id}")
    @ResponseBody
    public ResponseEntity<byte[]> getImage(@PathVariable("profile_id") UUID profile_id) throws Exception {
        Profile profile = profileService.getProfile(profile_id);
        byte[] imageData = profile.getProfile_picture();
        if (imageData == null || imageData.length == 0) {
            // Load default image from resources
            ClassPathResource defaultImage = new ClassPathResource("static/images/default-cover.jpeg");
            imageData = defaultImage.getInputStream().readAllBytes();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(imageData);
    }

    @GetMapping("/UserImage/{user_id}")
    @ResponseBody
    public ResponseEntity<byte[]> getUserImage(@PathVariable("user_id") UUID user_id) throws Exception {
        User user = userService.findById(user_id);
        Profile profile = profileRepo.findByUser(user);
        byte[] imageData = profile.getProfile_picture();
        if (imageData == null || imageData.length == 0) {
            // Load default image from resources
            ClassPathResource defaultImage = new ClassPathResource("static/images/default-cover.jpeg");
            imageData = defaultImage.getInputStream().readAllBytes();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(imageData);
    }

    @GetMapping("/coverImage")
    @ResponseBody
    public ResponseEntity<byte[]> getCoverImage(OAuth2AuthenticationToken user_auth) throws Exception {
        var userDetails = user_auth.getPrincipal().getAttributes();
        String email = userDetails.get("email").toString();
        User user = userService.findByEmail(email);
        Profile profile = profileRepo.findByUser(user);
        byte[] imageData = profile.getProfile_background();
        if (imageData == null || imageData.length == 0) {
            // Load default image from resources
            ClassPathResource defaultImage = new ClassPathResource("static/images/default-cover.jpeg");
            imageData = defaultImage.getInputStream().readAllBytes();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(imageData);
    }

    @GetMapping("/coverImage/{profile_id}")
    @ResponseBody
    public ResponseEntity<byte[]> getCoverImage(@PathVariable("profile_id") UUID profile_id) throws Exception {
        Profile profile = profileService.getProfile(profile_id);
        byte[] imageData = profile.getProfile_background();

        if (imageData == null || imageData.length == 0) {
            // Load default image from resources
            ClassPathResource defaultImage = new ClassPathResource("static/images/default-cover.jpeg");
            imageData = defaultImage.getInputStream().readAllBytes();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(imageData);
    }

    @GetMapping("/profile/mobile-search")
    public String mobileSearch() {
        return "mobileSearch";
    }



}
