package com.epita.social.service;

import com.epita.social.model.Comments;
import com.epita.social.model.Post;
import com.epita.social.model.Profile;
import com.epita.social.model.User;
import com.epita.social.payload.DTO.ProfileDTO;
import com.epita.social.repo.CommentsRepo;
import com.epita.social.repo.PostRepo;
import com.epita.social.repo.ProfileRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PostServiceImpl implements PostService {
    private final PostRepo postRepo;
    private final UserService userService;
    private final ProfileService profileService;
    private final CommentService commentService;
    private final ProfileRepo profileRepo;
    private final CloudinaryService cloudinaryService;
    private final CommentsRepo commentsRepo;


    @Override
    public Post addPost(Post post, ProfileDTO profile, List<MultipartFile> file) throws Exception {
        List<String> media_url_list = new ArrayList<>();
        String media_url = null;
        for (MultipartFile multipartFile : file) {
           media_url = cloudinaryService.upload_media_url(multipartFile);
           media_url_list.add(media_url);
        }
        post.setMediaUrls(media_url_list);
        post.setProfile_id(profile.getProfileId());
        post.setCaption(post.getCaption());
        post.setCreatedAt(LocalDateTime.now());
        post.setLocation(post.getLocation());
        post.setAuthor(profile.getUser().getFirstName()+" "+profile.getUser().getLastName());
        Post saved_post = postRepo.save(post);

        Profile profile1 = profileService.getProfile(profile.getProfileId());
        Set<Post> profile_post = profile1.getPosts();
        profile_post.add(saved_post);
        return saved_post;
    }

    @Override
    public void deletePost(UUID postId) throws Exception {
        postRepo.deleteById(postId);
    }

    @Override
    public Post getPostById(UUID postId) throws Exception {
        return postRepo.findById(postId).orElseThrow();
    }

    @Override
    public List<Post> getAllPosts() throws Exception {
        return postRepo.findAll();
    }

    @Override
    public void archivePost(UUID postId, Post post) throws Exception {
        Post archivePost = getPostById(postId);
        archivePost.setArchived(true);
        postRepo.save(archivePost);
    }

    @Override
    public void AddLike(UUID postID, UUID userId) throws Exception {
        Post post = getPostById(postID);
        User user = userService.findById(userId);
        Set<User> likes = post.getLiked();
        likes.add(user);
    }

    @Override
    public void AddComment(UUID postId, UUID userId, Comments comment) throws Exception {
        Post post = getPostById(postId);
        User user = userService.findById(userId);
        Profile profile = profileRepo.findByUser(user);
        Set<Comments> comments = post.getComments();
        comment.setPost(postId);
        comment.setProfile_id(profile.getProfile_id());
        Comments comments1 = commentService.addComment(comment, post, profile);
        comments.add(comments1);
    }

    @Override
    public void AddCommentLike(UUID postId, UUID commentId) throws Exception {
        Post post = getPostById(postId);
        Set<Comments> comments = post.getComments();
        for (Comments comment : comments) {
            if (comment.getCommentId().equals(commentId)) {
                long currentLikes = comment.getComment_likes();
                commentService.updateCountLikes(commentId, currentLikes);
                break;
            }
        }
    }

    @Override
    public void savePost(UUID post_id, UUID user_id) throws Exception {
        User user = userService.findById(user_id);
        Profile profile = profileRepo.findByUser(user);
        Post post = getPostById(post_id);
        Set<Post> saved_post = profile.getSavedPosts();
        saved_post.add(post);
    }

    @Override
    public List<Post> getFeed(OAuth2AuthenticationToken user_auth) throws Exception {
        var userDetails = user_auth.getPrincipal().getAttributes();
        String email = userDetails.get("email").toString();
        User user1 = userService.findByEmail(email);

        User currentUser = userService.findById(user1.getUserId());
        Profile currentProfile = profileRepo.findByUser(currentUser);

        Set<User> followingUsers = currentProfile.getFollowing();
        List<Post> allPosts = getAllPosts();
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


}
