package com.epita.social.service;

import com.epita.social.model.Comments;
import com.epita.social.model.Post;
import com.epita.social.model.Profile;
import com.epita.social.model.User;
import com.epita.social.repo.CommentsRepo;
import com.epita.social.repo.ProfileRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService{
    private final CommentsRepo commentsRepo;
    private final ProfileRepo profileRepo;
    private final UserService userService;

    @Override
    public Comments addComment(Comments comment, Post post, Profile profile) throws Exception {
        comment.setProfile_id(profile.getProfile_id());
        comment.setPost(post.getPost_id());
        Comments comments= commentsRepo.save(comment);
//        Set<Comments> comments1 = post.getComments();
//        comments1.add(comments);
        return comments;
    }

    @Override
    public List<Comments> getComments() throws Exception {
        return commentsRepo.findAll();
    }

    @Override
    public Comments getComment(UUID id) throws Exception {
        return commentsRepo.findById(id).orElseThrow();
    }

    @Override
    public void deleteComment(UUID id, UUID user_id) throws Exception {
        Comments comment = getComment(id);
        User user = userService.findById(user_id);
        Profile profile = profileRepo.findByUser(user);
        if(!comment.getProfile_id().equals(profile.getProfile_id())){
            throw new Exception("You are not allowed to delete this comment");
        }
        commentsRepo.deleteById(id);
    }

    @Override
    public void updateCountLikes(UUID id, long count) throws Exception {
        Comments comment = getComment(id);
        comment.setComment_likes(count+1);
        commentsRepo.save(comment);
    }

}
