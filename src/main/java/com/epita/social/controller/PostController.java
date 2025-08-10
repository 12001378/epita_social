package com.epita.social.controller;

import com.epita.social.mapper.profileMapper;
import com.epita.social.model.Comments;
import com.epita.social.model.Post;
import com.epita.social.model.Profile;
import com.epita.social.model.User;
import com.epita.social.payload.DTO.ProfileDTO;
import com.epita.social.repo.PostRepo;
import com.epita.social.repo.ProfileRepo;
import com.epita.social.service.CommentService;
import com.epita.social.service.PostService;
import com.epita.social.service.ProfileService;
import com.epita.social.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@ResponseBody
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class PostController {
    private final PostService postService;
    private final ProfileService profileService;
    private final UserService userService;
    private final ProfileRepo profileRepo;
    private final PostRepo postRepo;
    private final CommentService commentService;

    @GetMapping("/posts")
    public List<Post> getPosts() throws Exception {
        return postService.getAllPosts();
    }

    @GetMapping("/post/{post_id}")
    public Post getPostById(@PathVariable UUID post_id) throws Exception {
        return postService.getPostById(post_id);
    }

    @PostMapping(value = "/post", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Post addPost(@RequestPart("file") List<MultipartFile> file,
                        @RequestPart("post") String postJson, OAuth2AuthenticationToken user) throws Exception {
        var userDetails = user.getPrincipal().getAttributes();
        String email = userDetails.get("email").toString();
        User user1 = userService.findByEmail(email);
        ObjectMapper objectMapper = new ObjectMapper();
        Post post = objectMapper.readValue(postJson, Post.class);
        User user2 = userService.findById(user1.getUserId());
        Profile profile = profileRepo.findByUser(user2);
        ProfileDTO profileDTO = profileMapper.profileToProfileDTO(profile);
        return postService.addPost(post, profileDTO, file);
    }

    @GetMapping("/feed")
    public List<Post> getFeed(OAuth2AuthenticationToken user_auth) throws Exception {
        var userDetails = user_auth.getPrincipal().getAttributes();
        String email = userDetails.get("email").toString();
        User user1 = userService.findByEmail(email);

        User currentUser = userService.findById(user1.getUserId());
        Profile currentProfile = profileRepo.findByUser(currentUser);

        Set<User> followingUsers = currentProfile.getFollowing();
        List<Post> allPosts = postService.getAllPosts();

        List<Post> feedPosts = new ArrayList<>();

        List<UUID> followingProfileIds = followingUsers.stream()
                .map(user -> {
                    try {
                        return profileRepo.findByUser(user).getProfile_id();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());

        return postRepo.findFeedPosts(followingProfileIds, currentProfile.getProfile_id());

    }

    @PostMapping("/comment/{post_id}")
    public ResponseEntity<Comments> comment(@PathVariable("post_id") UUID post_id,
                                            OAuth2AuthenticationToken user_auth,
                                            @RequestBody Comments comments) throws Exception {
        var userDetails = user_auth.getPrincipal().getAttributes();
        String email = userDetails.get("email").toString();
        User user1 = userService.findByEmail(email);
        User currentUser = userService.findById(user1.getUserId());
        Profile currentProfile = profileRepo.findByUser(currentUser);
        Post post = postService.getPostById(post_id);

        postService.AddComment(post_id, user1.getUserId(), comments);
//        commentService.addComment(comments,post,currentProfile);

        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    @PostMapping("/like/{post_id}")
    public ResponseEntity<?> like_post(@PathVariable("post_id") UUID post_id, OAuth2AuthenticationToken user_auth) throws Exception {
        var userDetails = user_auth.getPrincipal().getAttributes();
        String email = userDetails.get("email").toString();
        User user1 = userService.findByEmail(email);
        postService.AddLike(post_id, user1.getUserId());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/comment-like/{post_id}/{comment_id}")
    public ResponseEntity<?> comment_like(@PathVariable("post_id") UUID post_id, @PathVariable("comment_id") UUID comment_id) throws Exception {
        postService.AddCommentLike(post_id, comment_id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/save/{post_id}")
    public ResponseEntity<?> save(@PathVariable("post_id") UUID post_id, OAuth2AuthenticationToken user_auth) throws Exception {
        var userDetails = user_auth.getPrincipal().getAttributes();
        String email = userDetails.get("email").toString();
        User user1 = userService.findByEmail(email);
        postService.savePost(post_id, user1.getUserId());
        return new ResponseEntity<>(HttpStatus.OK);
    }


}
