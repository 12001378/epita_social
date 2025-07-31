package com.epita.social.service;

import com.epita.social.model.Comments;
import com.epita.social.model.Post;
import com.epita.social.model.Profile;
import com.epita.social.model.User;

import java.util.List;
import java.util.UUID;

public interface CommentService {
    public Comments addComment(Comments comment, Post post, Profile profile) throws Exception;
    public List<Comments> getComments()throws Exception;
    public Comments getComment(UUID id)throws Exception;
    public void deleteComment(UUID id, UUID user_id)throws Exception;
    public void updateCountLikes(UUID id, long count)throws Exception;
}
