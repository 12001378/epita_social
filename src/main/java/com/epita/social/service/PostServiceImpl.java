

package com.epita.social.service;

import com.epita.social.events.CommentEvent;
import com.epita.social.events.LikeEvent;
import com.epita.social.events.NotificationEvent;
import com.epita.social.events.PostEvent;
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
    private final KafkaProducerService kafkaProducerService;


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
        
        // Send Kafka event for post creation
        PostEvent postEvent = new PostEvent(
            saved_post.getPost_id(),
            profile.getProfileId(),
            saved_post.getAuthor(),
            saved_post.getCaption(),
            saved_post.getLocation(),
            saved_post.getMediaUrls(),
            saved_post.getCreatedAt(),
            PostEvent.EventType.POST_CREATED
        );
        kafkaProducerService.sendPostEvent(postEvent);
        
        return saved_post;
    }

    @Override
    public void deletePost(UUID postId) throws Exception {
        Post post = getPostById(postId);
        postRepo.deleteById(postId);
        
        // Send Kafka event for post deletion
        PostEvent postEvent = new PostEvent(
            post.getPost_id(),
            post.getProfile_id(),
            post.getAuthor(),
            post.getCaption(),
            post.getLocation(),
            post.getMediaUrls(),
            post.getCreatedAt(),
            PostEvent.EventType.POST_DELETED
        );
        kafkaProducerService.sendPostEvent(postEvent);
    }

    @Override
    public Post getPostById(UUID postId) throws Exception {
        return postRepo.findById(postId).orElseThrow();
    }

    @Override
    public List<Post> getAllPosts() throws Exception {
    List<Post> posts = postRepo.findAll();
    posts.sort((a, b) -> {
        if (a.getCreatedAt() == null && b.getCreatedAt() == null) return 0;
        if (a.getCreatedAt() == null) return 1;
        if (b.getCreatedAt() == null) return -1;
        return b.getCreatedAt().compareTo(a.getCreatedAt());
    });
    return posts;
    }

    @Override
    public void archivePost(UUID postId, Post post) throws Exception {
        Post archivePost = getPostById(postId);
        archivePost.setArchived(true);
        postRepo.save(archivePost);
        
        // Send Kafka event for post archival
        PostEvent postEvent = new PostEvent(
            archivePost.getPost_id(),
            archivePost.getProfile_id(),
            archivePost.getAuthor(),
            archivePost.getCaption(),
            archivePost.getLocation(),
            archivePost.getMediaUrls(),
            archivePost.getCreatedAt(),
            PostEvent.EventType.POST_ARCHIVED
        );
        kafkaProducerService.sendPostEvent(postEvent);
    }

    @Override
    public void AddLike(UUID postID, UUID userId) throws Exception {
        Post post = getPostById(postID);
        User user = userService.findById(userId);
        Set<User> likes = post.getLiked();
        likes.add(user);
        
        // Send Kafka event for post like
        LikeEvent likeEvent = new LikeEvent(
            postID,
            userId,
            user.getFirstName() + " " + user.getLastName(),
            LocalDateTime.now(),
            LikeEvent.EventType.POST_LIKED
        );
        kafkaProducerService.sendLikeEvent(likeEvent);
        
        // Send notification event to post owner
        if (!post.getProfile_id().equals(profileRepo.findByUser(user).getProfile_id())) {
            NotificationEvent notificationEvent = new NotificationEvent(
                post.getProfile_id(),
                userId,
                user.getFirstName() + " " + user.getLastName() + " liked your post",
                NotificationEvent.NotificationType.POST_LIKED,
                postID,
                LocalDateTime.now()
            );
            kafkaProducerService.sendNotificationEvent(notificationEvent);
        }
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
        
        // Send Kafka event for comment creation
        CommentEvent commentEvent = new CommentEvent(
            comments1.getCommentId(),
            postId,
            profile.getProfile_id(),
            comments1.getComment(),
            user.getFirstName() + " " + user.getLastName(),
            LocalDateTime.now(),
            CommentEvent.EventType.COMMENT_CREATED
        );
        kafkaProducerService.sendCommentEvent(commentEvent);
        
        // Send notification event to post owner
        if (!post.getProfile_id().equals(profile.getProfile_id())) {
            NotificationEvent notificationEvent = new NotificationEvent(
                post.getProfile_id(),
                userId,
                user.getFirstName() + " " + user.getLastName() + " commented on your post",
                NotificationEvent.NotificationType.POST_COMMENTED,
                postId,
                LocalDateTime.now()
            );
            kafkaProducerService.sendNotificationEvent(notificationEvent);
        }
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
    profileRepo.save(profile);
    }

    @Override
    public List<Post> getFeed(OAuth2AuthenticationToken user_auth) throws Exception {
        var userDetails = user_auth.getPrincipal().getAttributes();
        String email = userDetails.get("email").toString();
        User user1 = userService.findByEmail(email);

        User currentUser = userService.findById(user1.getUserId());
        Profile currentProfile = profileRepo.findByUser(currentUser);

        Set<User> followingUsers = currentProfile.getFollowing();
        List<UUID> followingProfileIds = followingUsers.stream()
                .map(user -> {
                    try {
                        return profileRepo.findByUser(user).getProfile_id();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());

        List<Post> feedPosts = postRepo.findFeedPosts(followingProfileIds, currentProfile.getProfile_id());
        Set<Post> savedPosts = currentProfile.getSavedPosts();
        for (Post post : feedPosts) {
            post.setSavedByCurrentUser(savedPosts.contains(post));
        }
        return feedPosts;
    }
    @Override
    public void RemoveLike(UUID postID, UUID userId) throws Exception {
        Post post = getPostById(postID);
        User user = userService.findById(userId);
        Set<User> likes = post.getLiked();
        likes.remove(user);
        
        // Send Kafka event for post unlike
        LikeEvent likeEvent = new LikeEvent(
            postID,
            userId,
            user.getFirstName() + " " + user.getLastName(),
            LocalDateTime.now(),
            LikeEvent.EventType.POST_UNLIKED
        );
        kafkaProducerService.sendLikeEvent(likeEvent);
    }

    @Override
    public void unsavePost(UUID post_id, UUID user_id) throws Exception {
    User user = userService.findById(user_id);
    Profile profile = profileRepo.findByUser(user);
    Post post = getPostById(post_id);
    Set<Post> saved_post = profile.getSavedPosts();
    saved_post.remove(post);
    profileRepo.save(profile);
    }


}
