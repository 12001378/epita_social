package com.epita.social.service;

import com.epita.social.model.Comments;
import com.epita.social.model.Post;
import com.epita.social.model.Profile;
import com.epita.social.payload.DTO.ProfileDTO;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface PostService {
    Post addPost(Post post, ProfileDTO profile , List<MultipartFile> file) throws Exception;
    void deletePost(UUID postId) throws Exception;
    Post getPostById(UUID postId) throws Exception;
    List<Post> getAllPosts() throws Exception;
    void archivePost(UUID postId, Post post) throws Exception;
    void AddLike(UUID postID, UUID userId) throws Exception;
    void AddComment(UUID postId, UUID userId , Comments comments) throws Exception;
    void AddCommentLike(UUID postId,UUID comment_id) throws Exception;
    void savePost(UUID post_id,UUID user_id) throws Exception;
    List<Post> getFeed(OAuth2AuthenticationToken token) throws Exception;

}
